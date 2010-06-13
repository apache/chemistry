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
function list_objs($objs)
{
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
}
function check_response($client)
{
    if ($client->getLastRequest()->code > 299)
    {
        print "There was a problem with this request!\n";
        exit (255);
    }
}

$repo_url = $_SERVER["argv"][1];
$repo_username = $_SERVER["argv"][2];
$repo_password = $_SERVER["argv"][3];
$repo_folder = $_SERVER["argv"][4];
$repo_new_folder = $_SERVER["argv"][5];

$client = new CMISService($repo_url, $repo_username, $repo_password);
print "Connected\n";
$myfolder = $client->getObjectByPath($repo_folder);
print "Got Folder\n";
check_response($client);
if ($myfolder->properties['cmis:baseTypeId'] != "cmis:folder")
{
    print "NOT A FOLDER!\n";
    exit (255);
}

$my_new_folder = $client->createFolder($myfolder->id, $repo_new_folder);
check_response($client);

$obj_doc = $client->createDocument($my_new_folder->id, "TextFile.txt", array (), "THIS IS A NEW DOCUMENT", "text/plain");
check_response($client);

$obj_del = $client->createDocument($my_new_folder->id, "TextFileDel.txt", array (), "THIS IS A NEW DOCUMENT To Be Deleted", "text/plain");
check_response($client);

print "FOLDER AFTER CREATES\n=============================================\n";
$objs = $client->getChildren($my_new_folder->id);
check_response($client);
list_objs($objs);
print "=============================================\n\n";

$delContent = $client->getContentStream($obj_del->id);
echo "DEL CONTENT\n";
print $delContent . "\n";
print "\n";

echo "DELETEING " . $obj_del->properties['cmis:name'] . "\n";
$client->deleteObject($obj_del->id);
print "\n";

print "FOLDER AFTER DELETE\n=============================================\n";
$objs = $client->getChildren($my_new_folder->id);
check_response($client);
list_objs($objs);
print "=============================================\n\n";

$sub_folder = $client->createFolder($my_new_folder->id, "SUB_FOLDER");
$client->moveObject($obj_doc->id, $sub_folder->id, $my_new_folder->id);

print "FOLDER AFTER MOVE\n=============================================\n";
$objs = $client->getChildren($my_new_folder->id);
check_response($client);
list_objs($objs);
print "=============================================\n\n";

print "SUB-FOLDER AFTER MOVE\n=============================================\n";
$objs = $client->getChildren($sub_folder->id);
check_response($client);
list_objs($objs);
print "=============================================\n\n";
