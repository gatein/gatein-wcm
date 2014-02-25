CKEDITOR.plugins.add( 'uploads', {

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
            label: 'Select Upload',
            command: 'uploadsDialog'
        });

    }
});
