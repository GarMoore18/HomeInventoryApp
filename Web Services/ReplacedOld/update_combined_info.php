<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $con=mysqli_connect($host_name, $host_user, $host_pass, $db_name);

        $item_id = (int)$_POST['item_id'];
        $location_id = (int)$_POST['location_id'];
        $user_id = (int)$_POST['user_id'];
        $quantity = (int)$_POST['quantity'];

        // Create connection
        $con=mysqli_connect($host_name, $host_user, $host_pass, $db_name);
        
        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        // Select row where the barcode and location match request
        $sql = "SELECT `id`, `quantity` FROM `combined_info` WHERE
            `item_id` = $item_id AND `location_id` = $location_id";
        
        $result = mysqli_query($con, $sql);

        // There is a row, so update that row
        if (mysqli_num_rows($result) > 0) {
            $row = mysqli_fetch_assoc($result);

            $edit_id = $row['id'];
            $edit_quantity = (int)$row['quantity'];
            
            $new_quantity = $edit_quantity + $quantity;
            
            $update_sql = "UPDATE `combined_info` 
                            SET `user_id` = $user_id, `quantity` = $new_quantity
                            WHERE `id` = $edit_id";

            if(mysqli_query($con, $update_sql)) {
                echo 'U200';
            } else {
                echo 'U400';
            }
        
        // There is not a row, so insert new row
        } else {
            $Sql_Query = "INSERT INTO `combined_info`(`item_id`, `location_id`, `user_id`, `quantity`) 
                VALUES ($item_id, $location_id, $user_id, $quantity)";

            if(mysqli_query($con, $Sql_Query)) {
                echo 'I200';
            } else {
                echo 'I400';
            }
        }
        
        // Close connections
        mysqli_close($con);
    }
?>