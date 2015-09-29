<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="description" content="Database List">
<meta http-equiv="Pragma" content="no-cache">
<link href="styles.css" rel="stylesheet" type="text/css">
<title>7-Zip-JBinding voting</title>
</head>
<body>
<table>
	<tr>
		<td width="210"><?php
		function getRealIpAddr()
		{
			if (!empty($_SERVER['HTTP_CLIENT_IP']))   //check ip from share internet
			{
				$ip=$_SERVER['HTTP_CLIENT_IP'];
			}
			elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR']))   //to check ip is pass from proxy
			{
				$ip=$_SERVER['HTTP_X_FORWARDED_FOR'];
			}
			else
			{
				$ip=$_SERVER['REMOTE_ADDR'];
			}
			return $ip;
		}

		function isIpRegistered($vote_id, $ip) {
			$iptest = mysql_query("SELECT COUNT(*) FROM voteentry WHERE vote_id = $vote_id AND ip = '$ip'");
			$iptestrow = mysql_fetch_array($iptest, MYSQL_NUM);
			mysql_free_result($iptest);
			/*
			 if ($iptestrow[0] > 0) {
			 echo "IP ALREADY REGISTERED!: " . $iptestrow[0];
			 } else {
			 echo "IP NOT REGISTERED!: " . $iptestrow[0];
			 }
			 */
			return $iptestrow[0] > 0;
		}

		function getVoteCount($vote_id, $votechoice_id) {
			$voteentries = mysql_query("SELECT COUNT(*) FROM voteentry WHERE vote_id="
			. $vote_id . " AND votechoice_id=" . $votechoice_id);
			$voteentry = mysql_fetch_array($voteentries, MYSQL_NUM);
			mysql_free_result($voteentries);

			return $voteentry[0];
		}

		function isVoteOpened($vote_id) {
			$voteorders = mysql_query("SELECT `order` FROM vote WHERE id=$vote_id");
			$voteorder = mysql_fetch_array($voteorders, MYSQL_NUM);
			mysql_free_result($voteorders);

			return $voteorder[0] > 0;
		}


		//print_r($_POST);
		// echo "xx" . $_SERVER['REMOTE_ADDR'] . "xx<br>";
		// echo "xx" . getRealIpAddr() . "xx";
		$ip = getRealIpAddr();

		// echo "IP: $ip";

		$link = mysql_connect('mysql-s', 's210915rw', 'HeMySQ15');
		if (!$link) {
			die('DB connection error: ' . mysql_error());
		}
		mysql_select_db("s210915_main");


		$votes = mysql_query("SELECT id, question FROM vote WHERE `order` > 0 ORDER BY `order`");
		while ($vote = mysql_fetch_array($votes, MYSQL_NUM)) {

			echo '<div class="voting">';
			$voteentry_id = $_GET['vote' . $vote[0]];
			$ipRegistered = isIpRegistered($vote[0], $ip);
			$show_results = $ipRegistered || $voteentry_id || $_GET["results"];
			if ($show_results) {
				if (!$ipRegistered && $voteentry_id && isVoteOpened($vote[0])) {
					// Storing vote results
					$sql = "INSERT INTO voteentry (vote_id, votechoice_id, date, ip) "
					. "VALUES (". $vote[0] . ", $voteentry_id, NOW() , '$ip')";
					mysql_query($sql);
				}
			} else {
				echo '<form action="/voting.php" method="get" >';
			}

			echo '<table class="voting"><tr><td class="votequestion" colspan="2">';
			echo $vote[1];
			echo '<tr><td>';
			$votechoices = mysql_query("SELECT id, description FROM votechoice WHERE vote_id = " . $vote[0] ." ORDER BY `order`");

			while ($votechoice = mysql_fetch_array($votechoices, MYSQL_NUM)) {
				echo '<tr>';
				echo '<td class="votingdescr" valign="top">';
				if ($show_results) {
					$count = getVoteCount($vote[0], $votechoice[0]);
					echo "<b>$count</b> - ";
				} else {
					echo '<INPUT type="radio" name="vote'.$vote[0].'" value="'.$votechoice[0].'"><td class="votingdescr">';
				}
				echo $votechoice[1];

			}

			mysql_free_result($votechoices);

			if (!$show_results) {
				echo '<tr><td colspan="2"><INPUT type="submit" value="Send">';
			}
			echo "</table>";
			if (!$show_results) {
				echo '</form>';
			}
			echo '</div>';
			echo '<br>';
			echo '<br>';
		}

		mysql_free_result($votes);

		mysql_close($link);

		?></td>
	</tr>
</table>

</html>
