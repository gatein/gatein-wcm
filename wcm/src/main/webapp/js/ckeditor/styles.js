/**
 * Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

// This file contains style definitions that can be used by CKEditor plugins.
//
// The most common use for it is the "stylescombo" plugin, which shows a combo
// in the editor toolbar, containing all styles. Other plugins instead, like
// the div plugin, use a subset of the styles on their feature.
//
// If you don't have plugins that depend on this file, you can simply ignore it.
// Otherwise it is strongly recommended to customize this file to match your
// website requirements and design properly.

CKEDITOR.stylesSet.add( 'default', [
	/* Block Styles */

	// These styles are already available in the "Format" combo ("format" plugin),
	// so they are not needed here by default. You may enable them to avoid
	// placing the "Format" combo in the toolbar, maintaining the same features.
	/*
	{ name: 'Paragraph',		element: 'p' },
	{ name: 'Heading 1',		element: 'h1' },
	{ name: 'Heading 2',		element: 'h2' },
	{ name: 'Heading 3',		element: 'h3' },
	{ name: 'Heading 4',		element: 'h4' },
	{ name: 'Heading 5',		element: 'h5' },
	{ name: 'Heading 6',		element: 'h6' },
	{ name: 'Preformatted Text',element: 'pre' },
	{ name: 'Address',			element: 'address' },
	*/

    { name: 'Wcm List', element: 'wcm-list' },
    { name: 'Wcm Single', element: 'wcm-single' },
    { name: 'Wcm Param List', element: 'wcm-param-list' },
    { name: 'Wcm Param Single', element: 'wcm-param-single' },
    { name: 'Wcm Param Name', element: 'wcm-param-name' },
    { name: 'Wcm File List', element: 'wcm-file-list' },
    { name: 'Wcm Author', element: 'wcm-author' },
    { name: 'Wcm Content', element: 'wcm-content' },
    { name: 'Wcm Created', element: 'wcm-created' },
    { name: 'Wcm Description', element: 'wcm-description' },
    { name: 'Wcm Excerpt', element: 'wcm-excerpt' },
    { name: 'Wcm Filename', element: 'wcm-filename' },
    { name: 'Wcm Image', element: 'wcm-img' },
    { name: 'Wcm Iteration', element: 'wcm-iter' },
    { name: 'Wcm Link', element: 'wcm-link' },
    { name: 'Wcm Mimetype', element: 'wcm-mimetype' },
    { name: 'Wcm Title', element: 'wcm-title' },
    { name: 'Wcm Categories', element: 'wcm-categories' },
    { name: 'Wcm Category List', element: 'wcm-cat-list' },
    { name: 'Wcm Category Name', element: 'wcm-cat-name' },
    { name: 'Wcm Category Type', element: 'wcm-cat-type' },
    { name: 'Wcm Comments', element: 'wcm-comments' },
    { name: 'Wcm Comment Content', element: 'wcm-comment-content' },
    { name: 'Wcm Comment Author', element: 'wcm-comment-author' },
    { name: 'Wcm Comment Created', element: 'wcm-comment-created' },
    { name: 'Wcm Form Comments', element: 'wcm-form-comments' },
    { name: 'Wcm Form Content', element: 'wcm-form-content' },
    { name: 'Wcm Form Author', element: 'wcm-form-author' },
    { name: 'Wcm Form Email', element: 'wcm-form-email' },
    { name: 'Wcm Form Url', element: 'wcm-form-url' },
    { name: 'Wcm Form Button', element: 'wcm-form-button' },



	/* Inline Styles */

	// These are core styles available as toolbar buttons. You may opt enabling
	// some of them in the Styles combo, removing them from the toolbar.
	// (This requires the "stylescombo" plugin)
	/*
	{ name: 'Strong',			element: 'strong', overrides: 'b' },
	{ name: 'Emphasis',			element: 'em'	, overrides: 'i' },
	{ name: 'Underline',		element: 'u' },
	{ name: 'Strikethrough',	element: 'strike' },
	{ name: 'Subscript',		element: 'sub' },
	{ name: 'Superscript',		element: 'sup' },
	*/

	/* Object Styles */

	{
		name: 'Styled image (left)',
		element: 'img',
		attributes: { 'class': 'left' }
	},

	{
		name: 'Styled image (right)',
		element: 'img',
		attributes: { 'class': 'right' }
	},

	{
		name: 'Compact table',
		element: 'table',
		attributes: {
			cellpadding: '5',
			cellspacing: '0',
			border: '1',
			bordercolor: '#ccc'
		},
		styles: {
			'border-collapse': 'collapse'
		}
	},

	{ name: 'Borderless Table',		element: 'table',	styles: { 'border-style': 'hidden', 'background-color': '#E6E6FA' } },
	{ name: 'Square Bulleted List',	element: 'ul',		styles: { 'list-style-type': 'square' } }
]);

