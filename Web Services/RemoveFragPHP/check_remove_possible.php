<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);

        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        $barcode = $my_array_data['barcode'];
        //$barcode = '1111111111111';

        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        $select_stmt = $mysqli -> prepare("SELECT item_info.id as iid, item_info.name as iname, item_info.image, 
                                            possible_locations.id as lid, possible_locations.name as lname, 
                                            combined_info.quantity, combined_info.id as cid, combined_info.user_id 
                                            FROM combined_info 
                                            JOIN item_info ON combined_info.item_id = item_info.id 
                                            JOIN possible_locations ON combined_info.location_id = possible_locations.id 
                                            WHERE item_info.barcode = ?");

        $select_stmt -> bind_param('s', $barcode);

        if ($select_stmt -> execute()) {
			$select_stmt -> store_result();

            if ($select_stmt -> num_rows > 0) {
                $select_stmt -> bind_result($iid, $iname, $image, $lid, $lname, $quantity, $cid, $user_id);
                
                $result = array();
                while ($select_stmt -> fetch()) {
					$row = array('iid'=>$iid, 'iname'=>$iname, 'image'=>$image, 'lid'=>$lid, 
									'lname'=>$lname, 'quantity'=>$quantity, 'cid'=>$cid, 'user_id'=>$user_id);
                    array_push($result, $row);
                }

                header('Content-Type: application/json');
                // Encode the array to JSON and output the results
                echo json_encode($result);

            } else {
                echo json_encode(array(array("no_matches" => "true")));
            }
            $select_stmt->close();
        } else {
				
            header('Content-Type: application/json');
            // Encode the array to JSON and output the results
            echo json_encode(array(array("result_code" => 400)));
        }
    }
?>