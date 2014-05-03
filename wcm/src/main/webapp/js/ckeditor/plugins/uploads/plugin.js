CKEDITOR.plugins.add( 'uploads', {

    // Available language codes
    lang : ['en', 'es'],

    // Register the icons. They must match command names.
    icons: 'uploads',

    // The plugin initialization logic goes inside this method.
    init: function( editor ) {

        editor.addCommand( 'uploadsDialog', {
            // Define the function that will be fired when the command is executed.
            exec: function( editor ) {
                // Show select uploads dialog
                // This dialog is external to CKEditor library
                showSelectUploadsPost(editor.portalnamespace, editor);
            }
        });

        // Create the toolbar button that executes the above command.
        editor.ui.addButton( 'uploads', {
            label: editor.lang.uploads.button,
            command: 'uploadsDialog'
        });

    }
});
