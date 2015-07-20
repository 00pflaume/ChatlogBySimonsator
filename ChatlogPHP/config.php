<?php
mysql_connect ( 'localhost:3306', 'root', 'password' );
mysql_select_db ( "chatlog" );
$url="https://example.de";
$title_page="Example.de - Chatlog";//title of the Page
$title_image_url="https://fanart.tv/fanart/music/ddeb3502-8693-4619-b41d-263105f84477/musiclogo/example-4e0660c8549ac.png";
$title_header_text="Example.de";
$color="#1054B3";//color of the header
$heading_error="Erstellen eines Chatlogs";
$error_text="Wenn du meinst, dass jemand etwas in den Chat schreibt, was er nicht in den Chat schreiben sollte kannst du den Befehl /chatlog eingeben.
	Wenn du den Befehl eingegeben hast, dann erhälst du einen Link den du im <a href='https://example.de'>Forum</a> teilen kannst. 
	Solltest du den Befehl bereits eingeben haben und bist auf diese Seite gekommen, dann hast du wahrscheinlich den Link falsch geschrieben oder die Runde in der du denn Chatlog erstellt hast läuft noch.";
$footer1="<a href='https://example.de' class='footerLink'>&copy;Example.de</a>";
$footer2="Developer: Simon 'Simonsator' Brungs";
error_reporting(0);//set to one to get error messages
date_default_timezone_set ( 'Europe/Berlin' );
$language="de";//change this to en to set the language to english, anyway you need to change the error messages to the preferd language
?>