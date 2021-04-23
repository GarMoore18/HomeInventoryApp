<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='GET') {
        // Create connection
        $con=mysqli_connect($host_name, $host_user, $host_pass, $db_name);
        
        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        // Select all of our stocks from table 'stock_tracker'
        $sql = "SELECT * FROM `possible_locations`";
        
        // Confirm there are results
        if ($result = mysqli_query($con, $sql)) {
            // We have results, create an array to hold the results
                // and an array to hold the data
            $resultArray = array();
            $tempArray = array();
        
            // Loop through each result
            while($row = $result->fetch_object())
            {
                // Add each result into the results array
                $tempArray = $row;
                array_push($resultArray, $tempArray);
            }
        
            header('Content-Type: application/json');
            // Encode the array to JSON and output the results
            echo json_encode($resultArray);
        }
        
        // Close connections
        mysqli_close($con);
    }
?>