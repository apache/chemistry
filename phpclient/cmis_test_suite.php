<?php
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

require_once ('cmis_repository_wrapper.php');
$repo_url = $_SERVER["argv"][1];
$repo_username = $_SERVER["argv"][2];
$repo_password = $_SERVER["argv"][3];
$repo_folder = $_SERVER["argv"][4];
$repo_new_folder = $_SERVER["argv"][5];
$repo_debug = $_SERVER["argv"][6];

if ($repo_username == "alf_ticket")
{
    $client = new CMISService($repo_url, null, null, array (
        $repo_username => $repo_password
    ));
} else
{
    $client = new CMISService($repo_url, $repo_username, $repo_password);
}

if ($repo_debug)
{
    print "Repository Information:\n===========================================\n";
    print_r($client->workspace);
    print "\n===========================================\n\n";
}

$myfolder = $client->getObjectByPath($repo_folder);
print_r($client->getLastRequest());
if ($repo_debug)
{
    print "Folder Object:\n===========================================\n";
    print_r($myfolder);
    print "\n===========================================\n\n";
}

$myfolderType = $client->getObjectTypeDefinition($myfolder->id);
print_r($client->getLastRequest());
if ($repo_debug)
{
    print "Folder Type Def:\n===========================================\n";
    print_r($myfolderType);
    print "\n===========================================\n\n";
}

$my_new_folder = $client->createFolder($myfolder->id, $repo_new_folder);
print_r($client->getLastRequest());
if ($repo_debug)
{
    print "Return From Create Folder\n:\n===========================================\n";
    print_r($my_new_folder);
    print "\n===========================================\n\n";
}

$obj_doc = $client->createDocument($my_new_folder->id, "TextFile.txt", array (), "THIS IS A NEW DOCUMENT", "text/plain");
print_r($client->getLastRequest());
if ($repo_debug)
{
    print "Return From Create Document\n:\n===========================================\n";
    print_r($obj_doc);
    print "\n===========================================\n\n";
}

$obj_del = $client->createDocument($my_new_folder->id, "TextFileDel.txt", array (), "THIS IS A NEW DOCUMENT To Be Deleted", "text/plain");
if ($repo_debug)
{
    print "Return From Create Document\n:\n===========================================\n";
    print_r($obj_del);
    print "\n===========================================\n\n";
}

$objs = $client->getChildren($my_new_folder->id);
if ($repo_debug)
{
    print "Folder Children Objects\n:\n===========================================\n";
    print_r($objs);
    print "\n===========================================\n\n";
}

foreach ($objs->objectList as $obj)
{
    if ($obj->properties['cmis:baseTypeId'] == "cmis:document")
    {
        print "Document: " . $obj->properties['cmis:name'] . "\n";
    }
    elseif ($obj->properties['cmis:baseTypeId'] == "cmis:folder")
    {
        print "Folder: " . $obj->properties['cmis:name'] . "\n";
    } else
    {
        print "Unknown Object Type: " . $obj->properties['cmis:name'] . "\n";
    }
}

$delContent = $client->getContentStream($obj_del->id);
echo "DEL CONTENT\n";
print $delContent . "\n";

echo "DELETEING " . $obj_del->properties['cmis:name'] . "\n";
$client->deleteObject($obj_del->id);
$sub_folder = $client->createFolder($my_new_folder->id, "SUB_FOLDER");
$client->moveObject($obj_doc->id, $sub_folder->id, $my_new_folder->id);
print "MOVE REQUEST\n=============================================\n";
print_r($client->getLastRequest());

$objs = $client->getChildren($my_new_folder->id);
if ($repo_debug)
{
    print "Folder Children Objects\n:\n===========================================\n";
    print_r($objs);
    print "\n===========================================\n\n";
}

foreach ($objs->objectList as $obj)
{
    if ($obj->properties['cmis:baseTypeId'] == "cmis:document")
    {
        print "Document: " . $obj->properties['cmis:name'] . "\n";
    }
    elseif ($obj->properties['cmis:baseTypeId'] == "cmis:folder")
    {
        print "Folder: " . $obj->properties['cmis:name'] . "\n";
    } else
    {
        print "Unknown Object Type: " . $obj->properties['cmis:name'] . "\n";
    }
}

if ($repo_debug > 2)
{
    print "Final State of CLient:\n===========================================\n";
    print_r($client);
}
