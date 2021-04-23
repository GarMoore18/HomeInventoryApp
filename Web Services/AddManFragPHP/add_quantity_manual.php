<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);

        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        // For inserting into item_info
        $barcode = $my_array_data['barcode'];
        $name = $my_array_data['name'];

        $new_img = $my_array_data['image'];

        // For inserting into combined info
        $user_id = (int)$my_array_data['user_id'];
        $quantity = (int)$my_array_data['quantity'];
        $location_id = (int)$my_array_data['location_id'];

        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        // Add new item
        $new_item_stmt = $mysqli -> prepare("INSERT INTO `item_info`(`barcode`, `name`, `image`) 
												VALUES (?, ?, ?)");

        $new_item_stmt -> bind_param('ssb', $barcode, $name, $new_img);

		if ($new_item_stmt -> execute()) {
            $new_item_stmt->close();

            // Select new item
            $select_stmt = $mysqli -> prepare("SELECT * FROM `item_info` WHERE `barcode` = ?");

            $select_stmt -> bind_param('s', $barcode);
    
            if($select_stmt -> execute()) {
                $select_stmt -> store_result();
    
                if ($select_stmt -> num_rows > 0) {
                    $select_stmt -> bind_result($iid, $ibarcode, $iname, $iimage);
                    $select_stmt -> fetch();

                    $select_stmt->close();
                
                // Change the main table
                $insert_stmt = $mysqli -> prepare("INSERT INTO `combined_info`(`item_id`, `location_id`, `user_id`, `quantity`) 
                                                    VALUES (?, ?, ?, ?)");

                $insert_stmt -> bind_param('iiii', $iid, $location_id, $user_id, $quantity);

                if ($insert_stmt -> execute()) {
                    $result = array(array("result_code" => 200));
        
                } else{
                    $result = array(array("result_code" => 400));
                }
        
                header('Content-Type: application/json');
                // Encode the array to JSON and output the results
                echo json_encode($result);
        
                $insert_stmt->close();
				
				}

            } else {
                header('Content-Type: application/json');
                
                echo json_encode(array(array("result_code" => 12)));
            }

            $mysqli->close();

        } else {
            header('Content-Type: application/json');
            
            echo json_encode(array(array("result_code" => 123)));
        }
    }

?>