<?php

include("../includes/database_config.php");
    
    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $con=mysqli_connect($host_name, $host_user, $host_pass, $db_name);

        $barcode = $_POST['barcode'];
        $name = $_POST['name'];

        $Sql_Query = "INSERT INTO `item_info`(`barcode`, `name`) 
            VALUES ('$barcode', '$name')";

        //$Sql_Query = 'INSERT INTO `item_info`(`item_id`, `location_id`, `user_id`, `quantity`) 
            //VALUES (int($item_id),int($location_id),int($user_id),int($quantity))';

        if(mysqli_query($con, $Sql_Query)) {
            echo '200';
        } else {
            echo '400';
        }
        
        mysqli_close($con);
    }

?>