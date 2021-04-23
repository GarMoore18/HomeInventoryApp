<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);

        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        $iid = (int)$my_array_data['iid'];
        $user_id = (int)$my_array_data['user_id'];
        $quantity = (int)$my_array_data['quantity'];
        $location_id = (int)$my_array_data['location_id'];

        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }

		$select_stmt = $mysqli -> prepare("SELECT `id`, `quantity` FROM `combined_info` 
                                    WHERE `item_id` = ? AND `location_id` = ?");

        $select_stmt -> bind_param('ii', $iid, $location_id);

        if ($select_stmt -> execute()) {
            $select_stmt -> store_result();

            if ($select_stmt -> num_rows > 0) {
                $select_stmt -> bind_result($cid, $db_quan);
                $select_stmt -> fetch();

                $select_stmt->close();

                $new_quantity = $db_quan + $quantity;

                $update_sql = $mysqli -> prepare("UPDATE `combined_info` 
                                                    SET `user_id` = ?, `quantity` = ?
                                                    WHERE `id` = ?");
                
                $update_sql -> bind_param('iii', $user_id, $new_quantity, $cid);

                if ($update_sql -> execute()) {
                    $result = array(array("result_code" => 20));
            
                } else{
                    $result = array(array("result_code" => 40));
                }
        
                header('Content-Type: application/json');
                // Encode the array to JSON and output the results
                echo json_encode($result);

                $update_sql->close();

            } else {
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
            
        }

        $mysqli->close();
    } else {
        header('Content-Type: application/json');
        
        echo json_encode(array(array("result_code" => 1234)));
    }
?>