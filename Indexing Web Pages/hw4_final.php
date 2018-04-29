<?php

header('Content-Type: text/html; charset=utf-8');
$limit =10;
$query =isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$ranking == "solr";
$ranking = $_REQUEST['ranking'];
$additionalParameters = array('sort' => 'pageRankFile desc');
$results =false;
if($query)
{

require_once('Apache/Solr/Service.php');
$solr =new Apache_Solr_Service('localhost', 8983, '/solr/csci572/');
// if magic quotes is enabled then stripslashes will be needed
if(get_magic_quotes_gpc() ==1)
{
$query =stripslashes($query);
}

try
  {
    if($ranking == "solr")
      $results = $solr->search($query, 0, $limit);
    else if($ranking == "PageRank")
      $results = $solr->search($query, 0, $limit, $additionalParameters);

  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}
?>
<html>
<head>
<title>PHP Solr Client Example</title>
</head>
<body>
<form accept-charset="utf-8" method="get">
<label for="q"> Search:</label>
<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
<input type="radio" id="solr" name="ranking" value="solr" checked <?php echo isset($_GET["ranking"]) && $_GET["ranking"] == "solr"?"checked":"";?>/>Solr
      <input type="radio" id="PageRank" name="ranking" value="PageRank" <?php echo isset($_GET["ranking"]) && $_GET["ranking"] == "PageRank"?"checked":""; ?>/>PageRank
      
<input type="submit"/>
</form>
<?php
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
?>

    <?php if ($flag==1){ ?>
      <p> Showing results for :<a href="http://localhost/hw4.php?q=<?php echo urlencode($query); echo"&ranking=solr"; ?>"><?php echo $query;?></a></p>
      <p> Search Instead for :<a href="http://localhost/hw4.php?q=<?php echo urlencode($temporyQuery); echo"&ranking=solr"; ?>"><?php echo $temporyQuery;?></a></p>
    <?php }?>   


    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ol>
    <?php
  // iterate result documents
    foreach ($results->response->docs as $doc)
    {
    ?>
    <li>
      <table style="text-align: left">


  <?php

    // iterate document fields / values
    $title = $doc->title;
    $description = $doc->description;
    $date = $doc->dcterms_created;
    $url1 = $doc->id;


    $result1=$doc->description;
    if($result1==null)
    {
    $result1="NA";
    } 
  ?>

          <tr>
            <th><a href="<?php echo $doc->og_url; ?>" target="_blank"><?php echo $title; ?></a></th>
          </tr>

          <tr>
            <td><a style="color: green" href="<?php echo $doc->og_url; ?>" target="_blank"><?php echo $doc->og_url; ?></a></td>
          </tr>

          <tr>
            <td><?php echo $doc->id; ?></td>
          </tr>
          <tr>
            <td><?php echo $result1; ?></td>
          </tr>



<?php
?>
        </table>
      </li>
<?php
  }
?>
    </ol>
<?php
}
?>
</body>
</html>
