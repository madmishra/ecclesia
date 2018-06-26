scDefine(["dojo/text!./templates/SearchResult.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/widgets/IASBaseScreen", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link"], function(
templateText, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasRepeatingScreenUtils, _iasIASBaseScreen, _idxContentPane, _scplat, _scCurrencyDataBinder, _scBaseUtils, _scLabel, _scLink) {
    return _dojodeclare("extn.mobile.custom.search.SearchResultUI", [_iasIASBaseScreen], {
        templateString: templateText,
        uId: "extnorderSearchResult",
        packageName: "extn.mobile.custom.search",
        className: "SearchResult",
        title: "Title_SearchResult",
        screen_description: "Order Search Result screen",
        namespaces: {
            targetBindingNamespaces: [{
                value: 'getShipmentSearch_input',
                description: "The search criteria of shipment search.."
            }, {
                value: 'IncludeOrdersPickedInOtherStore',
                description: "The search criteria of shipment search."
            }],
            sourceBindingNamespaces: [{
                value: 'getShipmentList_output',
                description: "The list of organizations the user has access to find orders for."
            },
			{
                value: 'extn_mobile_getShipmentList_output',
                description: "The list of organizations the user has access to find orders for."
            }]
        },
        isDirtyCheckRequired: false,
        hotKeys: [],
        events: [],
        subscribers: {
            local: [{
                eventId: 'afterScreenInit',
                sequence: '25',
                description: 'This methods contains the screen initialization tasks.',
                handler: {
                    methodName: "initializeScreen"
                }
            }, {
                eventId: 'backToSearchLink_onClick',
                sequence: '30',
                description: 'This method is used to go back to Order Search screen',
                listeningControlUId: 'backToSearchLink',
                handler: {
                    methodName: "returnToOrderSearch",
                    description: ""
                }
            }, {
                eventId: 'repeatingIdentifierScreen_afterPagingLoad',
                sequence: '25',
                description: 'This event is triggered to call the after page load',
                listeningControlUId: 'repeatingIdentifierScreen',
                handler: {
                    methodName: "hideNavigationForSinglePageResult",
                    className: "RepeatingScreenUtils",
                    packageName: "ias.utils"
                }
            }]
        }
		});
});