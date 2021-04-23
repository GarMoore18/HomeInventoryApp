<?php
    
    include("../includes/database_config.php");

    //if ($_SERVER['REQUEST_METHOD']=='POST') {
        header('Content-Type: application/json');

        $mysqli = new mysqli($host_name, $host_user, $host_pass, $db_name);

        $data = file_get_contents('php://input');
        $my_array_data = json_decode($data, TRUE);

        // Check connection
        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
        
        $select_items = $mysqli -> prepare("SELECT * FROM item_info");

        $select_combined = $mysqli -> prepare("SELECT * FROM combined_info");
        
        $select_locations = $mysqli -> prepare("SELECT * FROM possible_locations");

        $select_users = $mysqli -> prepare("SELECT * FROM user_access");

        $result = array();
        if ($select_items -> execute()) {
			$select_items -> store_result();

            //$lid, $lname, $auid, $username, $password, $admin, $ciid, $clid, $cuid, $quantity, $cid, $last_mod
            if ($select_items -> num_rows > 0) {
                $select_items -> bind_result($iid, $barcode, $iname, $image);
                
                $items_result = array();
                while ($select_items -> fetch()) {
					$row = array('iid'=>$iid, 'barcode'=>$barcode, 'iname'=>$iname, 'image'=>$image);
                    array_push($items_result, $row);
                }
                // Encode the array to JSON and output the results
                //echo json_encode($result);
            } 
            array_push($result, $items_result);
            $select_items->close();
        } 
		if ($select_combined -> execute()) {
			$select_combined -> store_result();

            //$lid, $lname, $auid, $username, $password, $admin, $ciid, $clid, $cuid, $quantity, $cid, $last_mod
            if ($select_combined -> num_rows > 0) {
                $select_combined -> bind_result($cid, $ciid, $clid, $cuid, $quantity, $last_mod);
                
                $combined_result = array();
                while ($select_combined -> fetch()) {
					$row = array('cid'=>$cid, 'ciid'=>$ciid, 'clid'=>$clid, 'cuid'=>$cuid, 'quantity'=>$quantity, 'last_mod'=>$last_mod);
                    array_push($combined_result, $row);
                }
                // Encode the array to JSON and output the results
                //echo json_encode($result);
            } 
            array_push($result, $combined_result);
            $select_combined->close();
        } 
        if ($select_locations -> execute()) {
			$select_locations -> store_result();

            $locations_result = array();
            if ($select_locations -> num_rows > 0) {
                $select_locations -> bind_result($lid, $lname);

                while ($select_locations -> fetch()) {
					$row = array('lid'=>$lid, 'lname'=>$lname);
                    array_push($locations_result, $row);
                }
                // Encode the array to JSON and output the results
                //echo json_encode($result);
            } 
            array_push($result, $locations_result);
            $select_locations->close();
        } 
        if ($select_users -> execute()) {
			$select_users -> store_result();

            if ($select_users -> num_rows > 0) {
                $select_users -> bind_result($uid, $username, $password, $admin);
                
                $users_result = array();
                while ($select_users -> fetch()) {
					$row = array('uid'=>$uid, 'username'=>$username, 'password'=>$password, 'admin'=>$admin);
                    array_push($users_result, $row);
                }
                // Encode the array to JSON and output the results
                //echo json_encode($result);
            } 
            array_push($result, $users_result);
            $select_users->close();
        } 
        echo json_encode($result);
		
    //}
?>