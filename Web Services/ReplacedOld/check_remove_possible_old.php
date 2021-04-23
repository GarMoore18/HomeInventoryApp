<?php
    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
        $con=mysqli_connect($host_name, $host_user, $host_pass, $db_name);
        
        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        $barcode = $my_array_data['barcode'];
        //$barcode = '1111111111111';

        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        // Select all of our stocks from table 'stock_tracker'
        $sql = "SELECT item_info.id as iid, item_info.name as iname, item_info.image, 
                    possible_locations.id as lid, possible_locations.name as lname, 
                    combined_info.quantity, combined_info.id as cid, combined_info.user_id 
                    FROM combined_info 
                    JOIN item_info ON combined_info.item_id = item_info.id 
                    JOIN possible_locations ON combined_info.location_id = possible_locations.id 
                    WHERE item_info.barcode = '$barcode'";
        
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