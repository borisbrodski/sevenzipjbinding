<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="description" content="Database List">
<meta http-equiv="Pragma" content="no-cache">
<link href="styles.css" rel="stylesheet" type="text/css">
<title>7-Zip-JBinding release assemble and test files</title>
</head>
<body>
<h1>7-Zip-JBinding release assemble and test files</h1>
<table border="1" style="font-size: 13;">
	<tr>
		<td width="100">&nbsp;</td>
		<td>Id</td>
		<td>File name</td>
		<td>Size</td>
		<td>Timestamp</td>
		<td>IP</td>
		<td>Description</td>
	</tr>
	<tr>
	</tr>

	<?php
	$link = mysql_connect('mysql-s', 's210915rw', 'HeMySQ15');
	if (!$link) {
		die('DB connection error: ' . mysql_error());
	}
	mysql_select_db("s210915_main");

	$idToDelete = $_GET["rm"];
	if ($idToDelete) {
		$getFilenameQuery = "SELECT filename FROM files WHERE id = " . addslashes($idToDelete);
		$result = mysql_query($getFilenameQuery) or die("Error. Query 1 failed. " . mysql_error() . "\n");
		$row = mysql_fetch_array($result);
		if ($row) {
			list($filenameToDelete) = $row;
			$deletequery = "DELETE FROM files WHERE filename = '". addslashes($filenameToDelete) . "'";
			mysql_query($deletequery) or die("Error. Query 2 failed. " . mysql_error() . "\n");
		}
		mysql_free_result($result);
	}

	$files = mysql_query("SELECT f1.id, f1.filename,
							(SELECT SUM(OCTET_LENGTH(content)) FROM files f2 WHERE f1.filename = f2.filename) as `size`,
						  	f1.createtimestamp, f1.ip, f1.description, md5ok
						  FROM files f1
					      WHERE `chunk` = 1 ORDER BY `filename`") or die("Error. Query 3 failed.\n");

	while ($file = mysql_fetch_array($files)) {
		list($id, $name, $size, $timestamp, $ip, $description, $md5ok) = $file;
		if (!$description) {
			$description = "<i>no description</i>";
		}
		?>
	<tr>
		<td valign="middle">
		<form method="get"><input type="submit" value="-"
			style="font-size: 8;" />
			<?php 
				if (!$md5ok) {
					echo "<b><i>ERROR</i></b>";
				} ?>
			<input type="hidden" name="rm"
			value="<?php echo $id ?>" /></form>
		</td>
		<td><?php echo $id ?></td>
		<td><a href="download.php?filename=<?php echo urlencode($name) ?>"><?php echo $name ?></a></td>
		<td><?php echo $size ?></td>
		<td><?php echo $timestamp ?></td>
		<td><?php echo $ip ?></td>
		<td><?php echo $description ?></td>
	</tr>


	<?php
	}
	mysql_free_result($votechoices);



	mysql_close($link);

	?>

</table>
</body>
</html>
