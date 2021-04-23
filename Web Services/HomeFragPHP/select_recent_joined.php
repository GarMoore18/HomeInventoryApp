<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='GET') {
        // Create connection
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);
        
        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        $limit = 10;

        $select_stmt = $mysqli -> prepare("SELECT item_info.barcode, item_info.name as iid, item_info.image, 
                                            possible_locations.name as lid, user_access.username, 
                                            combined_info.quantity, combined_info.last_modified 
                                            FROM combined_info
                                            JOIN item_info ON combined_info.item_id = item_info.id
                                            JOIN possible_locations ON combined_info.location_id = possible_locations.id
                                            JOIN user_access ON combined_info.user_id = user_access.id
                                            ORDER BY combined_info.last_modified DESC
                                            LIMIT ?");
        
        $select_stmt -> bind_param('i', $limit);
        
        // Confirm there are results
        if ($select_stmt -> execute()) {
            $select_stmt -> store_result();

            if ($select_stmt -> num_rows > 0) {
                $select_stmt -> bind_result($barcode, $iid, $image, $lid, $username, $quantity, $modified);
                
                $result = array();
                while ($select_stmt -> fetch()) {
                    $row = array('barcode'=>$barcode, 'iid'=>$iid, 'image'=>$image, 'lid'=>$lid, 
                                    'username'=>$username,'quantity'=>$quantity, 'last_modified'=>$modified);
                    array_push($result, $row);
                }

                header('Content-Type: application/json');
                // Encode the array to JSON and output the results
                echo json_encode($result);

            }
            $select_stmt->close();
        } else {
                
            header('Content-Type: application/json');
            // Encode the array to JSON and output the results
            echo json_encode(array(array("result_code" => 400)));
        }
    }
?>