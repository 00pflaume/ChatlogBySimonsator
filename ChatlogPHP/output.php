<?php
mysql_query ( "SET NAMES 'utf8'" );
header('Content-Type: text/html; charset=utf-8');
if (! empty ( $_GET ['time'] ) && ! empty ( $_GET ['zufall'] )) {
	$zeit = $_GET ['time'];
	$zufall = $_GET ['zufall'];
	$abfrage = "SELECT Gechatlogt, Server, ID FROM chatlog_zuordnung WHERE Zeit = $zeit AND Zufall=$zufall LIMIT 1";
	$ergebnis = mysql_query ( $abfrage );
	$gefunden = false;
	while ( $row = mysql_fetch_object ( $ergebnis ) ) {
		$User = $row->Gechatlogt;
		$Server = $row->Server;
		$ID = $row->ID;
		$gefunden = true;
	}
	if ($gefunden == true) {
		$datum = $wochentag = date ( 'j.m.Y', $zeit );
		$Uhrzeit = $wochentag = date ( 'H', $zeit );
		if($language=="de"){
		echo "<h1 class='ueberschrifft'>Chatlog von " . $User . " vom " . $datum . " um " . $Uhrzeit . " Uhr auf dem Server ".$Server."</h1>";
		}
		else{
			echo "<h1 class='ueberschrifft'>Chatlog of " . $User . " on the " . $datum . " at " . $Uhrzeit . " On the server ".$Server."</h1>";
		}
		?>
		<div class="eigentlicherChatlog">
		<?php
		$abfrage = "SELECT Spieler, Zeit, Inhalt, Event FROM chatlog_speichern WHERE Zugehoerig = $ID";
		$ergebnis = mysql_query ( $abfrage );
		$durchlauf = 0;
		while ( $row = mysql_fetch_object ( $ergebnis ) ) {
			$Spieler [$durchlauf] = $row->Spieler;
			$Inhalt [$durchlauf] = $row->Inhalt;
			$geschenum [$durchlauf] = $row->Zeit;
			$Event [$durchlauf] = $row->Event;
			$durchlauf ++;
		}
		for($i = 0; $i < $durchlauf; $i ++) {
			if ($Event [$i] == 0) {
				$Uhr = $wochentag = date ( 'H:i:s', $geschenum [$i] );
				echo "[". $Uhr ."] ".$Spieler [$i] . ": " . $Inhalt [$i] . "<br>";
			}
			if ($Event [$i] == 1) {
				$Uhr = $wochentag = date ( 'H:i:s', $geschenum [$i] );
				if($language=="de"){
				echo $Spieler [$i] . " ist um " . $Uhr . " dem Spiel beigetreten<br>";
				}
				else{
					echo $Spieler [$i] . " joined at " . $Uhr . "<br>";
				}
			}
			if ($Event [$i] == 2) {
				$Uhr = $wochentag = date ( 'H:i:s', $geschenum [$i] );
				if($language=="de"){
				echo $Spieler [$i] . " hat das Spiel um " . $Uhr . " verlassen.<br>";
				}
				else{
					echo $Spieler [$i] . " has left the game at " . $Uhr . ".<br>";
				}
			}
			if ($Event [$i] == 3) {
				$Uhr = $wochentag = date ( 'H:i:s', $geschenum [$i] );
				if($language=="de"){
				echo "Ein Chatlog wurde f&uuml;r den Spieler " . $Inhalt [$i] . " von dem Spieler " . $Spieler [$i] . " um " . $Uhr . " erstellt<br>";
				}
				else{
					echo "A chatlog was created of " . $Inhalt [$i] . " from the player " . $Spieler [$i] . " at " . $Uhr . "<br>";
				}
			}
		}
	} else {
		echo "<h1 class='ueberschrifft'>".$heading_error."</h1>";
		?>
		<div class="eigentlicherChatlog">
		<?php
		echo $error_text;
	}
} else {
	echo "<h1 class='ueberschrifft'>".$heading_error."</h1>";?>
	<div class="eigentlicherChatlog">
	<?php
	echo $error_text;
}
mysql_close();
?>