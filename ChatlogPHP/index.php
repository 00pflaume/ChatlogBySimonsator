<?php
require "config.php";
mysql_query ( "SET NAMES 'utf8'" );
header('Content-Type: text/html; charset=utf-8');
?>
<html style="min-height: 100%">
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="style.css" type="text/css">
<title><?php echo $title_page;?></title>
<style type="text/css">
.kopf {
background-color: <?php echo $color;?>;
}
</style>
</head>
<body class="body">
	<div class="kopf">
		<nav class="navbar navbar-inverse navbar-static-top ng-scope"
			id="site-nav" role="navigation">
			<div class="container">
				<div align="center">
					<a class="navbar-brand" href="<?php echo $url;?>" ><img
						src="<?php echo $title_image_url;?>"
						width="310" alt="<?php echo $title_header_text;?>" class="logo"> 
				</a>
				</div>
				<div style="height: 0px;" class="navbar-collapse collapse"
					id="navbar-collapse-1">
					<ul class="nav navbar-nav navbar-right"></ul>
				</div>
			</div>
		</nav>
	</div>
	<div class="mitte">
<?php
require("output.php");
?>
</div>
</div>
<?php
include "footer.php";
?>
</body>
</html>