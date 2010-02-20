#
# Override these settings with values to match your environment.
#
# CMIS repository's service URL
REPOSITORY_URL = 'http://cmis.alfresco.com/s/cmis'
#REPOSITORY_URL = 'http://localhost:8080/alfresco/s/cmis'
# CMIS repository credentials
USERNAME = 'admin'
PASSWORD = 'admin'
# Absolute path to a directory where test folders can be created, including
# the trailing slash.
TEST_ROOT_PATH = '/jeff test/' # REMEMBER TRAILING SLASH
# Binary test files. Assumed to exist in the same dir as this python script
TEST_BINARY_1 = '250px-Cmis_logo.png'
TEST_BINARY_2 = 'sample-a.pdf'
# For repositories that may index test content asynchronously, the number of
# times a query is retried before giving up.
MAX_FULL_TEXT_TRIES = 10
# The number of seconds the test should sleep between tries.
FULL_TEXT_WAIT = 10
