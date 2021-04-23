<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        // Create connection
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);

        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        //$radio_selected = "Quantity"; // Will be the text of the selected radio button        
        //$description = "";
        //$quantity = 54;
        //$quan_oper = "=";
        //$location = "";
        
        $radio_selected = $my_array_data['radio'];
        $description = "%{$my_array_data['description']}%";
        $location = $my_array_data['location'];
        $quantity = $my_array_data['quantity'];
        $quan_oper = $my_array_data['operator'];

        if ($radio_selected == "Description") {
            $returned = descriptionOnly($description);
        } elseif ($radio_selected == "Quantity") {
            $returned = quantityOnly($quantity, $quan_oper);
        } elseif ($radio_selected == "Location") {
            $returned = locationOnly($location);
        }

        header('Content-Type: application/json');
        // Encode the array to JSON and output the results
        echo json_encode($returned);
    }

    function descriptionOnly($description) {
        global $mysqli;
        
        $sql = $mysqli -> prepare("SELECT item_info.barcode, item_info.name as iid, item_info.image, 
                        possible_locations.name as lid, user_access.username, 
                        combined_info.quantity, combined_info.last_modified 
                    FROM combined_info
                    JOIN item_info ON combined_info.item_id = item_info.id
                    JOIN possible_locations ON combined_info.location_id = possible_locations.id
                    JOIN user_access ON combined_info.user_id = user_access.id
                    WHERE item_info.name LIKE ?
                    ORDER BY combined_info.last_modified DESC");

        $sql -> bind_param('s', $description);

        $result = array();
        if ($sql -> execute()) {
            $sql -> store_result();

            if ($sql -> num_rows > 0) {
                $sql -> bind_result($barcode, $iid, $image, $lid, $username, $quantity, $modified);
                
                while ($sql -> fetch()) {
                    $row = array('barcode'=>$barcode, 'iid'=>$iid, 'image'=>$image, 'lid'=>$lid, 
                                    'username'=>$username,'quantity'=>$quantity, 'last_modified'=>$modified);
                    array_push($result, $row);
                }
            } else {
                $result = array(array("no_matches" => "true"));
            }
            $sql->close();
        } else {
            $result = array(array("result_code" => 400));
        }

        return $result;
    };

    function chooseQuanSQL($quan_oper) {
        if ($quan_oper == ">") {
            $sql_use = "SELECT item_info.barcode, item_info.name as iid, item_info.image, 
                            possible_locations.name as lid, user_access.username, 
                            combined_info.quantity, combined_info.last_modified 
                        FROM combined_info
                        JOIN item_info ON combined_info.item_id = item_info.id
                        JOIN possible_locations ON combined_info.location_id = possible_locations.id
                        JOIN user_access ON combined_info.user_id = user_access.id
                        WHERE combined_info.quantity >= ?
                        ORDER BY combined_info.last_modified DESC";
        } elseif ($quan_oper == "<") {
            $sql_use = "SELECT item_info.barcode, item_info.name as iid, item_info.image, 
                            possible_locations.name as lid, user_access.username, 
                            combined_info.quantity, combined_info.last_modified 
                        FROM combined_info
                        JOIN item_info ON combined_info.item_id = item_info.id
                        JOIN possible_locations ON combined_info.location_id = possible_locations.id
                        JOIN user_access ON combined_info.user_id = user_access.id
                        WHERE combined_info.quantity <= ?
                        ORDER BY combined_info.last_modified DESC";
        } elseif ($quan_oper == "=") {
            $sql_use = "SELECT item_info.barcode, item_info.name as iid, item_info.image, 
                            possible_locations.name as lid, user_access.username, 
                            combined_info.quantity, combined_info.last_modified 
                        FROM combined_info
                        JOIN item_info ON combined_info.item_id = item_info.id
                        JOIN possible_locations ON combined_info.location_id = possible_locations.id
                        JOIN user_access ON combined_info.user_id = user_access.id
                        WHERE combined_info.quantity = ?
                        ORDER BY combined_info.last_modified DESC";
        }
        return $sql_use;
    }

    function quantityOnly($quantity, $quan_oper) {
        global $mysqli;
        
        $sql = $mysqli -> prepare(chooseQuanSQL($quan_oper));

        $sql -> bind_param('i', $quantity);

        $result = array();
        if ($sql -> execute()) {
            $sql -> store_result();
            
            if ($sql -> num_rows > 0) {
                $sql -> bind_result($barcode, $iid, $image, $lid, $username, $quantity, $modified);
                
                while ($sql -> fetch()) {
                    $row = array('barcode'=>$barcode, 'iid'=>$iid, 'image'=>$image, 'lid'=>$lid, 
                                    'username'=>$username,'quantity'=>$quantity, 'last_modified'=>$modified);
                    array_push($result, $row);
                }
            } else {
                $result = array(array("no_matches" => "true"));
            }
            $sql->close();
        } else {
            $result = array(array("result_code" => 400));
        }

        return $result;
    };

    function locationOnly($location) {
        global $mysqli;
        
        $sql = $mysqli -> prepare("SELECT item_info.barcode, item_info.name as iid, item_info.image, 
                        possible_locations.name as lid, user_access.username, 
                        combined_info.quantity, combined_info.last_modified 
                    FROM combined_info
                    JOIN item_info ON combined_info.item_id = item_info.id
                    JOIN possible_locations ON combined_info.location_id = possible_locations.id
                    JOIN user_access ON combined_info.user_id = user_access.id
                    WHERE combined_info.location_id = ?
                    ORDER BY combined_info.last_modified DESC");

        $sql -> bind_param('i', $location);

        $result = array();
        if ($sql -> execute()) {
            $sql -> store_result();

            if ($sql -> num_rows > 0) {
                $sql -> bind_result($barcode, $iid, $image, $lid, $username, $quantity, $modified);
                
                while ($sql -> fetch()) {
                    $row = array('barcode'=>$barcode, 'iid'=>$iid, 'image'=>$image, 'lid'=>$lid, 
                                    'username'=>$username,'quantity'=>$quantity, 'last_modified'=>$modified);
                    array_push($result, $row);
                }
            } else {
                $result = array(array("no_matches" => "true"));
            }
            $sql->close();
        } else {
            $result = array(array("result_code" => 400));
        }

        return $result;
    };
?>