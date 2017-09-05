/*!
 * Ajax Bootstrap Select
 *
 * Extends existing [Bootstrap Select] implementations by adding the ability to search via AJAX requests as you type. Originally for CROSCON.
 *
 * @version 1.4.1
 * @author Adam Heim - https://github.com/truckingsim
 * @link https://github.com/truckingsim/Ajax-Bootstrap-Select
 * @copyright 2017 Adam Heim
 * @license Released under the MIT license.
 *
 * Contributors:
 *   Mark Carver - https://github.com/markcarver
 *
 * Last build: 2017-07-21 1:08:53 PM GMT-0400
 */
!(function ($) {
/*
 * Note: You do not have to load this translation file. English is the
 * default language of this plugin and is compiled into it automatically.
 *
 * This file is just to serve as the default string mappings and as a
 * template for future translations.
 * @see: ./src/js/locale/en-US.js
 *
 * Four character language codes are supported ("en-US") and will always
 * take precedence over two character language codes ("en") if present.
 *
 * When copying this file, remove all comments except the one above the
 * definition objection giving credit to the translation author.
 */

/*!
 * English translation for the "en-US" and "en" language codes.
 * Mark Carver <mark.carver@me.com>
 */
$.fn.ajaxSelectPicker.locale['zh-CN'] = {
    /**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} currentlySelected = 'Currently Selected'
     * @markdown
     * The text to use for the label of the option group when currently selected options are preserved.
     */
    currentlySelected: '当前选择',

    /**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} emptyTitle = 'Select and begin typing'
     * @markdown
     * The text to use as the title for the select element when there are no items to display.
     */
    emptyTitle: '未选择',

    /**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} errorText = ''Unable to retrieve results'
     * @markdown
     * The text to use in the status container when a request returns with an error.
     */
    errorText: '无法搜索结果',

    /**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} searchPlaceholder = 'Search...'
     * @markdown
     * The text to use for the search input placeholder attribute.
     */
    searchPlaceholder: '搜索...',

    /**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} statusInitialized = 'Start typing a search query'
     * @markdown
     * The text used in the status container when it is initialized.
     */
    statusInitialized: '输入搜索查询',

    /**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} statusNoResults = 'No Results'
     * @markdown
     * The text used in the status container when the request returns no results.
     */
    statusNoResults: '没有结果',

    /**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} statusSearching = 'Searching...'
     * @markdown
     * The text to use in the status container when a request is being initiated.
     */
    statusSearching: '搜索中...',

	/**
     * @member $.fn.ajaxSelectPicker.locale
     * @cfg {String} statusToShort = 'Please enter more characters'
     * @markdown
     * The text used in the status container when the request returns no results.
     */
    statusTooShort: '请输入更多字符'
};
$.fn.ajaxSelectPicker.locale.zh = $.fn.ajaxSelectPicker.locale['zh-CN'];
})(jQuery);
