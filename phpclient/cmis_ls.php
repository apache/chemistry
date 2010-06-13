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
$repo_debug = $_SERVER["argv"][5];

$client = new CMISService($repo_url, $repo_username, $repo_password);

if ($repo_debug)
{
    print "Repository Information:\n===========================================\n";
    print_r($client->workspace);
    print "\n===========================================\n\n";
}

$myfolder = $client->getObjectByPath($repo_folder);
if ($repo_debug)
{
    print "Folder Object:\n===========================================\n";
    print_r($myfolder);
    print "\n===========================================\n\n";
}

$objs = $client->getChildren($myfolder->id);
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
