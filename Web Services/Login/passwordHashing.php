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

        $login = FALSE;
        $options = ['cost' => 10];

		// Check connection
		if (mysqli_connect_errno()) {
			echo "Failed to connect to MySQL: " . mysqli_connect_error();
		}
		
		$account_sel = $mysqli -> prepare("SELECT `id`, `password`, `admin` FROM `user_access` WHERE `username` = ?");
		
		$account_sel -> bind_param('s', $username);
		
        if ($account_sel -> execute()) {
            $result = $account_sel -> get_result();
			
			$resultArray = array();
			$tempArray = array();
			
			while ($row = $result -> fetch_assoc()) {
				// Add each result into the results array
				if (password_verify($password, $row['password'])) {
                    $login = TRUE;
                    
                    if (password_needs_rehash($row['password'], PASSWORD_DEFAULT, $options)) {
                        $hash = password_hash($password, PASSWORD_DEFAULT, $options);

                        $update_hash = $mysqli -> prepare("UPDATE `user_access` SET `password` =  ? WHERE `id` = ?");
                        $update_hash -> bind_param('si', $hash, $row['id']);

                        if ($update_hash -> execute()) {
                            echo '0 ';
                        } else {
                            '1 ';
                        }
                    }
                }
			}
        }
		$account_sel->close();

        if ($login) {
            echo '0';
        } else {
            echo '1';
        }
	}

?>