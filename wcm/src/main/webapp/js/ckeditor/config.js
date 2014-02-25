/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';

// Toolbar configuration generated automatically by the editor based on config.toolbarGroups.
    config.toolbar = [
        { name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Templates' ] },
        { name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', '-', 'Undo', 'Redo' ] },
        { name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll' ] },
        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', '-', 'RemoveFormat' ] },
        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl' ] },
        '/',
        { name: 'wcm', items: [ 'uploads' ] },
        { name: 'links', items: [ 'Link', 'Unlink' ] },
        { name: 'insert', items: [ 'Image', 'Flash', 'Table', 'HorizontalRule', 'SpecialChar', 'PageBreak'] },
        { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
        { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
        { name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] },
        { name: 'others', items: [ '-' ] }
    ];

    // Disable CKEditor content filter
    config.allowedContent = true;

    // To show WCM uploads dialog
    config.extraPlugins = 'uploads';

    config.templates_replaceContent = false;

    // Encoding
    config.entities = false;
    config.basicEntities = false;

    config.entities_greek = false;
    config.entities_latin = false;

    config.entities_additional = '';
    config.htmlEncodeOutput = false;
};

// Adding these rules to templates indentation
CKEDITOR.on( 'instanceReady', function( ev )
{
    var writer = ev.editor.dataProcessor.writer;
    writer.indentationChars = '  ';
    writer.selfClosingEnd = ' />';

    var dtd = CKEDITOR.dtd;

    for ( var e in CKEDITOR.tools.extend( {'wcm-list':1,
                                           'wcm-param-list':1,
                                           'wcm-single':1,
                                           'wcm-param-single':1,
                                           'wcm-file-list':1,
                                           'wcm-content':1,
                                           'wcm-cat-list':1,
                                           'wcm-categories':1,
                                           'wcm-comments':1,
                                           'wcm-form-comments':1,
                                           'wcm-form-content':1}, dtd.$block) )
    {
        ev.editor.dataProcessor.writer.setRules( e, {
            indent : true,
            breakBeforeOpen : true,
            breakAfterOpen : true,
            breakAfterClose : true,
            breakBeforeClose : true
        });
    }
    for ( var e in CKEDITOR.tools.extend( {'wcm-author':1,
                                           'wcm-created':1,
                                           'wcm-description':1,
                                           'wcm-excerpt':1,
                                           'wcm-filename':1,
                                           'wcm-img':1,
                                           'wcm-iter':1,
                                           'wcm-link':1,
                                           'wcm-mimetype':1,
                                           'wcm-title':1,
                                           'wcm-cat-name':1,
                                           'wcm-cat-type':1,
                                           'wcm-param-name':1,
                                           'wcm-comment-content':1,
                                           'wcm-comment-author':1,
                                           'wcm-comment-created':1,
                                           'wcm-form-author':1,
                                           'wcm-form-email':1,
                                           'wcm-form-url':1,
                                           'wcm-form-button':1}, dtd.$inline ) )
    {
        ev.editor.dataProcessor.writer.setRules( e, {
            indent : true,
            breakBeforeOpen : false,
            breakAfterOpen : false,
            breakAfterClose : true,
            breakBeforeClose : false
        });
    }

});