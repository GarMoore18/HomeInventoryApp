<?php

    include("../includes/database_config.php");
    
    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $con=mysqli_connect($host_name, $host_user, $host_pass, $db_name);

        $item_id = (int)$_POST['item_id'];
        $location_id = (int)$_POST['location_id'];
        $user_id = (int)$_POST['user_id'];
        $quantity = (int)$_POST['quantity'];

        $Sql_Query = "INSERT INTO `combined_info`(`item_id`, `location_id`, `user_id`, `quantity`) 
            VALUES ($item_id, $location_id, $user_id, $quantity)";

        if(mysqli_query($con, $Sql_Query)) {
            echo '200';
        } else {
            echo '400';
        }
        
        mysqli_close($con);
    }

?>