<?php

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

 include 'simple_html_dom.php';
include 'SpellCorrector.php';
ini_set('memory_limit',-1);
//echo SpellCorrector::correct("musk");

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;

function searchsnippet($path, $var_desc, $query)
    {
        $text = file_get_html($path);
        $ret = finding($text, $query, $var_desc);
        if($ret){return $ret;}
        else
        {$query_terms = preg_split('/\s*[,:;!?.-\s+\t]\s*/', $query);   
            foreach($query_terms as $term)
            { $ret = finding($text, $term, $var_desc);
               if($ret)
                {return $ret;}}}
        return "";
    }

if ($query)
{


  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
require_once('/var/www/html/solr-php-client-master/Apache/Solr/Service.php');
$solr = new Apache_Solr_Service('localhost', 8983, '/solr/csci572/');

  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }

  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)

  try
  {

echo("<script>console.log('PHP: ".$query."');</script>");
if ($_GET['searchtype']=="default"){
$additionalParameters = array(
'fl' => array('id','description','title','og_url')
);
}
else{
$additionalParameters = array(
'fl' => array('id','description','title','og_url'),
'sort'=>"pageRankFile desc"
);
}
//copying original string before modifying

$orginal=trim($query);
echo("<script>console.log('Inside iF!!');</script>");
//call only when spelling correction is required
if((isset($_GET['noSpell'])&&$_GET['noSpell']=="true"))
{
echo("<script>console.log('Inside NO SPELL');</script>");
unset($_GET['noSpell']);
$results = $solr->search($query, 0, $limit,$additionalParameters);
}


else if(!(isset($_GET['spell'])&&$_GET['spell']=="fal"))
{
//splitting query at spaces
$crumbs=explode(" ",trim($query));


$endresult="";

//for each crumb find correct spelling

foreach($crumbs as $each){

echo("<script>console.log('Each: ".$each."');</script>");
$each=SpellCorrector::correct($each);
echo("<script>console.log('Each: ".$each."');</script>");
$endresult=$endresult." ".$each;
}

echo("<script>console.log('Endresult: ".$endresult."');</script>");

//modify query based on correct spelling

$endresult=trim($endresult);
$query=$endresult;
echo("<script>console.log('Query-->: ".$query."');</script>");
}



$results = $solr->search($query, 0, $limit,$additionalParameters);


  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }

function find_url($inp){

$ret="";
if (($handle = fopen("/var/www/html/WPMap.csv", "r")) !== FALSE) {
    while (($data = fgetcsv($handle)) !== FALSE) {

	if($data[0] == $inp)
		{

		$ret=$data[1];
		break;
       		}
       
       
    }
    fclose($handle);
    return $ret;
}
}

}



function HighlightQuery($query,$line)
{echo("<script>console.log('Endresult: ".$query."');</script>");
 $query=explode(" ",$query);
 for ($i=0;$i<sizeof($query);$i++)
   {

   $line=str_ireplace($query[$i],"<strong>".$query[$i]."</strong>",$line);
   }
 echo("<script>console.log('line: ".$line."');</script>");
 return $line;
}




function finding($text, $query, $var_desc)
    {
//check if query term is present

     if(stripos($var_desc, $query) !== false){return $var_desc.'<br>';}
     else{
//if not extract the text and try finding a match
            $textopt = $text->plaintext;
            $splitted = preg_split( "/[.\n]/", $textopt );
	    foreach($splitted as $line)
            {
            if (stripos($line, $query) !== false)
            {
            if(strlen($line) > 100)//length of query must be more than 100
            	 {return $line;}}}}
        return 0;}

    ?>
<html>
  <head>
    <title>PHP Solr Client Example........</title>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="//resources/demos/styles.css" >
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/jquery-ui.min.js"></script>

<style>
ul.ui-autocomplete{
list-style:none;
}
.ui-helper-hidden-accessible{display:none;}
</style>

<script type="text/javascript">
$(function() {
 var constantlink = "http://localhost:8983/solr/csci572/suggest?q=";
var URL;
 $("#query").autocomplete({
 source : function(request, response) {
var querystring=$("#query").val();
if(querystring.search(' ')>0)
{

var extra=querystring.substring((querystring.lastIndexOf(' '))+1);
if(extra.trim()=="")
URL=constantlink+$("#query").val() +"&wt=json";
else
URL =constantlink +extra+ "&wt=json";
}

else
URL = constantlink + $("#query").val() + "&wt=json";

 $.ajax({
 url : URL,
 success : function(data) {
var x=[];
var check=$("#query").val();
if(check.search(' ')>0)
{

var ind=check.lastIndexOf(' ');
 var extra=check.substring(0,ind+1);
var output=check.substring(ind+1);
if(output.trim()!='')
{
tsuggestions=data['suggest']['suggest'][output]['numFound'];

for($i=0;$i<tsuggestions;$i++)
{
var opt=check.substring(ind+1);
var sug=data['suggest']['suggest'][opt]['suggestions'][$i]['term'];
if(opt.trim()!='' && sug.indexOf('.') == -1)
{x.push(extra+''+data['suggest']['suggest'][opt]['suggestions'][$i]['term']);}}}
else
x.push(extra);
}
else
{
tsuggestions=data['suggest']['suggest'][$("#query").val()]['numFound'];
for($i=0;$i<tsuggestions;$i++)
{
var sug=data['suggest']['suggest'][$("#query").val()]['suggestions'][$i]['term'];
if(sug.indexOf('.') == -1){x.push(data['suggest']['suggest'][$("#query").val()]['suggestions'][$i]['term']);}}}
response(x);
 },
 dataType : 'jsonp',
 jsonp:'json.wrf'
 });},minLength : 1})
 });
</script>
  </head>
  <body style="width:100%">
    <form  accept-charset="utf-8" method="get" style="text-align:center">
      <label for="q" style="font-size:150%">Search:</label>
      <input id="query" name="q" type="text" value="<?php echo htmlspecialchars($orginal, ENT_QUOTES, 'utf-8'); ?>"/><br />
	<input type="radio" name="searchtype" value="default" <?php if(isset($_GET['searchtype'])&&$_GET['searchtype']=="default") echo "checked";?>>Lucene Solr
	<input type="radio" name="searchtype" value="pagerank" <?php if(isset($_GET['searchtype'])&&$_GET['searchtype']=="pagerank") echo "checked";?> >Page Rank<br/>
	<input type="submit"/>
    </form>

<?php
if((!isset($_GET['spell'])) && (strcasecmp($query,$orginal)!=0))
{
echo (' <div style="color:#646865;font-size:125%"> showing results for <u style="color:#124599;font-size:125%">'. $query.'</u></div> <div style="color:#646865;font-size:110%"> instead of <a href="work.php?q='.$orginal.'&searchtype='.$_GET['searchtype'].'&spell=fal&noSpell=true" style="font-size:110%">'.$orginal .'</a></div><br />');
}
?>


<?php

// display results
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
?>
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ol>
<?php
  // iterate result documents

 foreach ($results->response->docs as $doc)
 {


$json = json_decode($doc, true);
echo '<pre>';
echo $json->id;
?>

<table text-align: left"> 
<?php
 foreach ($doc as $field => $value)
 {
?>
<?php { 
?>
 <li><tr > 
<?php if($doc->title != ""){ ?>
<td><a target = "_blank" style="font-size:16px;" href = "<?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8');?>">
 <?php  
$titleVal=$doc->title; 
echo $titleVal; ?></a></td>
<?php }
else {  ?>
<td><?php echo $notApplicable; } ?></td>
</tr>
<tr> 

<?php if($doc->og_url != ""){ ?>
 <td><a target="_blank" style="color:green; font-size:16px;" href="<?php $urlVal=$doc->og_url; echo htmlspecialchars($urlVal, ENT_NOQUOTES, 'utf-8');?>">
 <?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8'); ?></a></td>
<?php }
else { ?>
<td><?php echo $notApplicable; } ?></td>
</tr>
<tr> 
<td style="font-size:16px"><?php if($doc->id != "") {echo $doc->id;} else {echo $notApplicable;} ?></td></tr>
<tr> <td style="font-size:16px"><?php if($doc->description != "") {if(sizeof($doc->description)>1) {$descriptionVal=implode('.',$doc->description);} else {$descriptionVal=$doc->description;}
echo $descriptionVal;} else {$descriptionVal="" ; echo $notApplicable;} ?></td></tr>

<tr> 
<td style="font-size:16px"><?php echo("<script>console.log('Query before snippet ".$query."');</script>");echo  ("Snippet:  ".HighlightQuery($query,searchsnippet($doc->id,$descriptionVal,$query))); ?> </td>

</tr>
</li>
<?php }
 ?>
<?php 
break;
?>
<?php } ?>
</tbody></table> 
 
<?php
 }
?>
 </ol>
<?php
}
?>
 </body>
</html>
