<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        // Create connection
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);

        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        $barcode = $my_array_data['barcode'];
		//$barcode = '1111111111111';

        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        // Select all of our stocks from table 'stock_tracker'
        $sql = $mysqli -> prepare("SELECT item_info.barcode, item_info.name as iid, item_info.image, 
                        possible_locations.name as lid, user_access.username, 
                        combined_info.quantity, combined_info.last_modified 
                    FROM combined_info
                    JOIN item_info ON combined_info.item_id = item_info.id
                    JOIN possible_locations ON combined_info.location_id = possible_locations.id
                    JOIN user_access ON combined_info.user_id = user_access.id
                    WHERE item_info.barcode = ?
                    ORDER BY combined_info.last_modified DESC");
        
        $sql -> bind_param('s', $barcode);
        
        // Confirm there are results
        if ($sql -> execute()) {
            $sql -> store_result();

            if ($sql -> num_rows > 0) {
                $sql -> bind_result($barcode, $iid, $image, $lid, $username, $quantity, $modified);
                
                $result = array();
                while ($sql -> fetch()) {
                    $row = array('barcode'=>$barcode, 'iid'=>$iid, 'image'=>$image, 'lid'=>$lid, 
                                    'username'=>$username,'quantity'=>$quantity, 'last_modified'=>$modified);
                    array_push($result, $row);
                }

                header('Content-Type: application/json');
                // Encode the array to JSON and output the results
                echo json_encode($result);

            } else {
                //header('Content-Type: application/json');
                // Encode the array to JSON and output the results
                echo json_encode(array(array("no_matches" => "true")));
                }

            $sql->close();
        } else {
                
            header('Content-Type: application/json');
            // Encode the array to JSON and output the results
            echo json_encode(array(array("result_code" => 400)));
        }
    }
?>