<?php

    //Used to login a user    
    include("../includes/database_config.php");

    if ($_SERVER['REQUEST_METHOD']=='POST') {
		$mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);
    
        $username = $_POST['username'];
        $password = $_POST['password'];
        //$username = 'David';
        //$password = 'Garrett';
        //$hash = password_hash($password, PASSWORD_DEFAULT);

        $register = FALSE;
        $options = ['cost' => 10];

		// Check connection
		if (mysqli_connect_errno()) {
			echo "Failed to connect to MySQL: " . mysqli_connect_error();
		}
		
		$account_create = $mysqli -> prepare("INSERT INTO `user_access` VALUES (?, ?, ?)");
		
        $hash = password_hash($password, PASSWORD_DEFAULT, $options);

		$account_create -> bind_param('ssi', $username, $hash, 0);
		
        if ($account_create -> execute()) {
            try {
                $result = $account_create -> get_result();
                
                $resultArray = array();

                echo '0';

            } catch (Exception $e){
                $error = $e->getMessage();
                echo '1';
            }

        }
		$account_create->close();
	}

?>