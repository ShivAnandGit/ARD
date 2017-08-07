set -x
#APP_ID="317391"
#API_USERNAME="apidigitalsandbox2"
#API_PASSWORD=""

echo APP_ID is $APP_ID
echo API_USERNAME is $API_USERNAME

# Directory argument
 if [[ "$1" != "" ]]; then
	UPLOAD_DIR="$1"
else
	echo "[-] Directory not specified."
	exit 1
 fi

# Check if directory exists
 if ! [[ -d "$UPLOAD_DIR" ]]; then
	echo "[-] Directory does not exist"
	exit 1
fi

# Version argument
if [[ "$2" != "" ]]; then
	VERSION="$2"
else
	VERSION=`date "+%Y-%m-%d %T"`	# Use date as default
fi

RESULTS_DIR=${WORKSPACE}/veracodeResults
TODAY=`date "+%Y%m%d"`

DETAILED_REPORT_PDF_FILE="$RESULTS_DIR"/"veracode.detailed.$TODAY.pdf"
DETAILED_REPORT_XML_FILE="$RESULTS_DIR"/"veracode.detailed.$TODAY.xml"
SUMMARY_REPORT_PDF_FILE="$RESULTS_DIR"/"veracode.summary.$TODAY.pdf"

PRESCAN_SLEEP_TIME=300
SCAN_SLEEP_TIME=300

# Validate HTTP response
function validate_response {
	local response="$1"

	# Check if response is XML
	if ! [[ "$response" =~ (\<\?xml version=\"1\.0\" encoding=\"UTF-8\"\?\>) ]]; then
		echo "[-] Response body is not XML format at `date`"
		echo "$response"
		exit 1
	fi

	# Check for an error element
	if [[ "$response" =~ (<error>[a-zA-Z0-9 \.]+</error>) ]]; then
		local error=$(echo $response | sed -n 's/.*<error>\(.*\)<\/error>.*/\1/p')
		echo "[-] Error: $error"
		exit 1
	fi
}

# Checks the state of the application prior to attempting to create a new build and download the previous build if available
function check_state {
	echo "[+] Checking application build state"
	local build_info_response=`curl --silent --compressed -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/5.0/getbuildinfo.do -F "app_id=$APP_ID"`
	validate_response "$build_info_response"

	if  [[ "$build_info_response" =~ (status=\"Results Ready\" engine_version=\"[0-9]+\"/\>) ]]; then
		VC_BUILD_ID=$(echo $build_info_response | sed -n 's/.* build_id=\"\([0-9]*\)\" .*/\1/p')

		echo "[+] Previous scan build id -  $VC_BUILD_ID"
    export VC_BUILD_ID
	fi
}


# Download reports
function download {
	if ! [[ -d "$RESULTS_DIR" ]]; then
		mkdir -p "$RESULTS_DIR"
	fi

	echo "[+] Downloading detailed report PDF"
	curl --compressed -o "$DETAILED_REPORT_PDF_FILE" -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/4.0/detailedreportpdf.do?build_id=$VC_BUILD_ID

	echo "[+] Downloading detailed report XML"
	curl --compressed -o "$DETAILED_REPORT_XML_FILE" -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/4.0/detailedreport.do?build_id=$VC_BUILD_ID

	echo "[+] Downloading summary report PDF"
	curl --compressed -o "$SUMMARY_REPORT_PDF_FILE" -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/4.0/summaryreportpdf.do?build_id=$VC_BUILD_ID

	# Validate files were downloaded
	if ! [[ -f $DETAILED_REPORT_PDF_FILE ]]; then
		echo "[-] Detailed PDF report failed to download"
		exit 1
	fi

	if ! [[ -f $DETAILED_REPORT_XML_FILE ]]; then
		echo "[-] Detailed XML report failed to download"
		exit 1
	fi

	if ! [[ -f $SUMMARY_REPORT_PDF_FILE ]]; then
		echo "[-] Summary PDF report failed to download"
		exit 1
	fi
}

# Create new build
function createbuild {
	echo "[+] Creating a new Veracode build named \"$VERSION\" for application #$APP_ID"

	local create_build_response=`curl --silent --compressed -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/4.0/createbuild.do -F "app_id=$APP_ID&version=$VERSION"`
	validate_response "$create_build_response"

	# Extract build id
	VC_BUILD_ID=$(echo $create_build_response | sed -n 's/.* build_id=\"\([0-9]*\)\" .*/\1/p')
}

# Upload files
function uploadfiles {
	for file in $UPLOAD_DIR/*
	do
		if [[ -f "$file" ]]; then
			echo "[+] Uploading $file"
			local upload_file_response=`curl --compressed -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/5.0/uploadfile.do -F "app_id=$APP_ID" -F "file=@$file"`
			validate_response "$upload_file_response"
		fi
	done

	# Validate all files were successfully uploaded
	for file in $UPLOAD_DIR/*
	do
		if [[ -f "$file" ]]; then
			if ! [[ "$upload_file_response" =~ (\<file file_id=\"[0-9]+\" file_name=\""${file##*/}"\" file_status=\"Uploaded\"/\>) ]]; then
				echo "[-] Error uploading $file"
				exit 1
			fi
		fi
	done
}

# Begin pre-scan
function beginprescan {
	echo "[+] Starting pre-scan of uploaded files"
	local pre_scan_response=`curl --silent --compressed -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/5.0/beginprescan.do -F "app_id=$APP_ID" -F "auto_scan=true"`
	validate_response "$pre_scan_response"
}

# Poll pre-scan status
function pollprescan {
	echo "[+] Polling pre-scan status every $PRESCAN_SLEEP_TIME seconds"
	local is_pre_scanning=true
	while $is_pre_scanning; do
		sleep $PRESCAN_SLEEP_TIME

		local build_info_response=` curl --silent --compressed -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/5.0/getbuildinfo.do -F "app_id=$APP_ID"`
		validate_response "$build_info_response"

		# Check if pre-scan is successful
		if [[ "$build_info_response" =~ (\<analysis_unit analysis_type=\"Static\" status=\"Pre-Scan Success\" engine_version=\"[0-9]+\"/\>) ]]; then
			is_pre_scanning=false
			echo -e "\n[+] Pre-scan complete"
		else
			echo -n -e "."
		fi
	done
}

# Begin application scan
function beginscan {
	echo "[+] Starting scan of uploaded files"
	local begin_scan_response=`curl --silent --compressed -u "$API_USERNAME:$API_PASSWORD" https://analysiscenter.veracode.com/api/5.0/beginscan.do  -F "app_id=$APP_ID&scan_all_top_level_modules=true"`
	validate_response "$begin_scan_response"
}




echo "Init - `date`"

check_state

download

createbuild

uploadfiles

beginprescan

#pollprescan

#beginscan



echo "Complete - `date`"

exit 0
