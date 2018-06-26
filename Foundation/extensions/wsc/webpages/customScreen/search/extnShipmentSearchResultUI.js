scDefine(["dojo/text!./templates/extnShipmentSearchResult.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/widgets/IASBaseListScreen", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!extn/customScreen/search/extnShipmentDetails"], function(
templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasRepeatingScreenUtils, _iasIASBaseListScreen, _idxContentPane, _scplat, _scButtonDataBinder, _scBaseUtils, _extnextnShipmentDetails) {
    return _dojodeclare("extn.customScreen.search.extnShipmentSearchResultUI", [_iasIASBaseListScreen], {
        templateString: templateText,
        uId: "extnShipmentSearchResult",
        packageName: "extn.customScreen.search",
        className: "extnShipmentSearchResult",
        title: "",
        screen_description: "This screen is container for shipment search results.",
        repeatingscreenID: 'extn.customScreen.search.extnShipmentDetails',
        requiresPagination: true,
        detailsWizard: 'wsc.components.shipment.summary.ShipmentSummaryWizard',
        repeatingPath: 'Shipments.Shipment',
        detailsEditor: 'wsc.desktop.editors.ShipmentEditor',
        isDirtyCheckRequired: true,
        namespaces: {
            targetBindingNamespaces: [],
            sourceBindingNamespaces: [{
                value: 'LST_listAPIInput',
                description: "This namespace is used to store the input used to load the data in the repeating screen. It is used when the Repeating screen is refreshed."
            }, {
                value: 'getList_output',
                description: "Model used to save getList api output."
            }, {
                value: 'popupOutput',
                description: "Stores popup output set from setPopupOutput"
            }]
        },
        hotKeys: [{
            id: "Popup_btnCancel",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "Popup_btnCancel",
            invocationContext: "",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
        events: [{
            name: 'callListApi'
        }],
        subscribers: {
            global: [{
                eventId: 'beforeContainerDialogClosed',
                sequence: '25',
                description: 'Subscriber for before Container dailog closed event',
                handler: {
                    methodName: "onPopupClose"
                }
            }],
            local: [{
                eventId: 'callListApi',
                sequence: '25',
                description: 'This event is should be triggered to call the list api',
                handler: {
                    methodName: "LST_executeApi",
                    description: ""
                }
            }, {
                eventId: 'repeatingPanel_afterPagingLoad',
                sequence: '25',
                description: 'This event is triggered to call the after page load',
                handler: {
                    methodName: "afterPagingLoad",
                    description: ""
                }
            }, {
                eventId: 'repeatingPanel_afterPagingLoad',
                sequence: '20',
                description: 'This event is triggered to call the after page load',
                listeningControlUId: 'repeatingPanel',
                handler: {
                    methodName: "hideNavigationForSinglePageResult",
                    className: "RepeatingScreenUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '25',
                description: 'Subscriber for after Screen Init event',
                handler: {
                    methodName: "initScreen"
                }
            }, {
                eventId: 'Popup_btnNext_onClick',
                sequence: '25',
                description: 'Next / Confirm Button Action',
                listeningControlUId: 'Popup_btnNext',
                handler: {
                    methodName: "onPopupConfirm",
                    description: ""
                }
            }, {
                eventId: 'Popup_btnCancel_onClick',
                sequence: '25',
                description: 'Cancel/Close Button Action',
                listeningControlUId: 'Popup_btnCancel',
                handler: {
                    methodName: "onPopupClose",
                    description: ""
                }
            }]
        }
    });
});