# Retrieving JBOSS_HOME
. $(pwd)/../setup.properties

src="target/gatein-wcm-impl-sec-0.0.1-SNAPSHOT.jar"
if [ -f "$src" ]; then
    dest="$JBOSS_HOME/modules/org/modeshape/main"
    echo "Copying $src into $dest"
    mkdir -p "$dest"
    cp "$src" "$dest"
fi
