<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);

        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        $id = (int)$my_array_data['id'];
        $user_id = (int)$my_array_data['user_id'];
        $quantity = (int)$my_array_data['quantity'];

        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }

        $update_sql = $mysqli -> prepare("UPDATE `combined_info` 
                                            SET `user_id` = ?, `quantity` = ?
                                            WHERE `id` = ?");

        $update_sql -> bind_param('iii', $user_id, $new_quantity, $cid);

        // Confirm there are results
        if ($update_sql -> execute()) {
            $result = array(array("result_code" => 200));
    
        } else{
            $result = array(array("result_code" => 400));
        }

        header('Content-Type: application/json');
        // Encode the array to JSON and output the results
        echo json_encode($result);

        $update_sql->close();
        
        $mysqli->close();
    }
?>