# Script to download example images
IMG_URL="https://raw.github.com/jboss-jdf/ticket-monster/master/demo/src/main/resources/import.sql"
TMP_DIR="/tmp/wcm"
IMG_DIR="./images"

# Create TEMP dir and IMG dir
mkdir -p $TMP_DIR
mkdir -p $IMG_DIR

# Get SQL file with images URLs
wget -O $TMP_DIR/img.sql $IMG_URL

# Extract image url and download
IMAGES=$(grep -o "'http.*'" $TMP_DIR/img.sql | sed -n 's/\x27\(.*\)\x27/\1/p')
for F in $IMAGES
do
  wget -P $IMG_DIR $F
done
