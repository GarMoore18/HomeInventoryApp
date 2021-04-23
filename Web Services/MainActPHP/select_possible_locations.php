<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='GET') {
        // Create connection
        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);
        
        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }

        // Select all of our stocks from table 'stock_tracker'
        $sql = "SELECT * FROM `possible_locations`";

        $sql = $mysqli -> prepare("SELECT * FROM `possible_locations`");
        
        if ($sql -> execute()) {
            $sql -> store_result();

            if ($sql -> num_rows > 0) {
                $sql -> bind_result($id, $name);
                
                $result = array();
                while ($sql -> fetch()) {
                    $row = array('id'=>$id, 'name'=>$name);
                    array_push($result, $row);
                }

                header('Content-Type: application/json');
                // Encode the array to JSON and output the results
                echo json_encode($result);

            }

            $sql->close();
        } else {
            header('Content-Type: application/json');
            // Encode the array to JSON and output the results
            echo json_encode(array(array("result_code" => 400)));
        }

        $mysqli->close();
    }
?>