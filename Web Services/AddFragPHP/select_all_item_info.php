<?php
    
    include("../includes/database_config.php");

	if ($_SERVER['REQUEST_METHOD']=='POST') {
		$mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);
		
		// Check connection
		if (mysqli_connect_errno()) {
			echo "Failed to connect to MySQL: " . mysqli_connect_error();
		}
		
		$stmt = $mysqli -> prepare("SELECT * FROM `item_info` WHERE `barcode` = ?");
		
		$stmt -> bind_param('s', $_POST['barcode']);
		
		if ($stmt -> execute()) {
			$result = $stmt -> get_result();
			
			$resultArray = array();
			$tempArray = array();
			
			while ($row = $result -> fetch_assoc()) {
				// Add each result into the results array
				$tempArray = $row;
				array_push($resultArray, $tempArray);
			}

			header('Content-Type: application/json');
			// Encode the array to JSON and output the results
			echo json_encode($resultArray);
		} else {
			header('Content-Type: application/json');
			
			echo json_encode(array(array("trash" => 1234)));
		}
		
		$stmt->close();
	}
?>