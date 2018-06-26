scDefine(["dojo/text!./templates/CustomEditor.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/BaseTemplateUtils", "scbase/loader!ias/utils/EditorScreenUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!ias/widgets/IASBaseEditor", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Image","scbase/loader!sc/plat/dojo/widgets/Screen","scbase/loader!sc/plat/dojo/widgets/Editor", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link","scbase/loader!ias/utils/WidgetUtils"], function(
templateText, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasBaseTemplateUtils, _iasEditorScreenUtils, _iasUIUtils, _iasIASBaseEditor, _idxContentPane, _scplat, _scCurrencyDataBinder, _scImageDataBinder, _scAdvancedTableLayout, _scBaseUtils, _scImage, _scLabel, _scLink, _scEditor) {
    return _dojodeclare("extn.editors.MyOrderEditor", [_iasIASBaseEditor], {
        templateString: templateText,
        uId: "MyOrderEditor",
        packageName: "extn.editors",
        className: "CustomEditor",
        subtitle:"subs",
        screen_description: "Screen",
        namespaces: {
            targetBindingNamespaces: [],
            sourceBindingNamespaces: [{
                value: 'InitialEditorInput',
                description: "Holds the initial editor input passed into the editor on open."
            }]
        },
        isRTscreenLoaded: 'null',
        newScreenData: null,
        editorTitleValue: 'editor title value',
        isSearchEditor: false,
        isRTscreeninitialized: 'null',
        editorTitle: 'Custom',
        isWizardinitialized: 'null',
        comparisonAttributes: [],
        hotKeys: [],
        events: [{
            name: 'setSystemMessage'
        }, {
            name: 'resizeEditor'
        }, {
            name: 'setEditorInput'
        },  {
            name: 'beforeEditorClosed'
        }, {
            name: 'afterRTScreenStartup'
        }],
        subscribers: {
            local: [{
                eventId: 'closeSystemMessage_onClick',
                sequence: '25',
                description: 'Handler for closing system message',
                handler: {
                    methodName: "closeMessagePanel",
                    className: "BaseTemplateUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'setEditorInput',
                sequence: '25',
                description: 'Handler for set editor input event',
                handler: {
                    methodName: "setEditorInput"
                }
            }, {
                eventId: 'beforeEditorClosed',
                sequence: '25',
                description: 'Handler for before editor closed event',
                handler: {
                    methodName: "handleEditorClose",
                    className: "UIUtils",
                    packageName: "ias.utils",
                    description: ""
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '25',
                handler: {
                    methodName: "onScreenInit"
                }
            }, {
                eventId: 'linkclose_onClick',
                sequence: '30',
                description: 'Handler for close customer message panel',
                listeningControlUId: 'linkclose',
                handler: {
                    methodName: "closeCustomerMessagePanel",
                    className: "BaseTemplateUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'siteMapLink_onClick',
                sequence: '30',
                description: 'Handler for view site map',
                listeningControlUId: 'siteMapLink',
                handler: {
                    methodName: "openSiteMap",
                    className: "EditorScreenUtils",
                    packageName: "ias.utils",
                    description: ""
                }
            }],
        showRelatedTaskScreenHolder: function(
        event, bEvent, ctrl, args) {
            _isccsEditorRelatedTaskUtils.showRelatedTaskScreenHolder(
            this);
           
        },
        showOrHideRelTask: function(
        event, bEvent, ctrl, args) {
            _isccsEditorRelatedTaskUtils.showOrHideRelatedTaskScreen(
            this);
        }

		}

    });
});