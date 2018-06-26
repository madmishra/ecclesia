scDefine(["dojo/text!./templates/extnShipmentSearch.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/SearchUtils", "scbase/loader!ias/widgets/IASBaseSearchScreen", "scbase/loader!idx/form/CheckBox", "scbase/loader!idx/form/CheckBoxList", "scbase/loader!idx/form/FilteringSelect", "scbase/loader!idx/form/RadioButtonSet", "scbase/loader!idx/form/TextBox", "scbase/loader!idx/layout/ContentPane", "scbase/loader!idx/layout/TitlePane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder", "scbase/loader!sc/plat/dojo/binding/CheckBoxListDataBinder", "scbase/loader!sc/plat/dojo/binding/ComboDataBinder", "scbase/loader!sc/plat/dojo/binding/RadioSetDataBinder", "scbase/loader!sc/plat/dojo/binding/SimpleDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/ControllerWidget", "scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget", "scbase/loader!extn/customScreen/search/extnShipmentSearchResultInitController"], function (
    templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasSearchUtils, _iasIASBaseSearchScreen, _idxCheckBox, _idxCheckBoxList, _idxFilteringSelect, _idxRadioButtonSet, _idxTextBox, _idxContentPane, _idxTitlePane, _scplat, _scButtonDataBinder, _scCheckBoxDataBinder, _scCheckBoxListDataBinder, _scComboDataBinder, _scRadioSetDataBinder, _scSimpleDataBinder, _scBaseUtils, _scControllerWidget, _scIdentifierControllerWidget, _extnextnShipmentSearchResultInitController) {
    return _dojodeclare("extn.customScreen.search.extnShipmentSearchUI", [_iasIASBaseSearchScreen], {
        templateString: templateText,
        uId: "shipmentSearchScreen",
        packageName: "extn.customScreen.search",
        className: "extnShipmentSearch",
        title: "blank",
        screen_description: "This provides the capability to search for shipments based on search criteria.",
        customValidationClassName: '',
        rootInputElement: 'Shipment',
        listScreenUID: 'extnShipmentSearchResult',
        customValidationPackage: '',
        advancedSearchInputNamespace: 'getAdvancedShipmentList_input',
        customValidationErrorMessage: '',
        customValidationMethodName: '',
        defaultOrderBy: '',
        isDirtyCheckRequired: false,
        namespaces: {
            targetBindingNamespaces: [{
                seq: '1',
                value: 'pickShipOrBoth',
                description: "The search criterita of whether we are searching for pickup, shipment, or both orders."
            }, {
                seq: '1',
                value: 'getAdvancedShipmentList_input',
                description: "The search criteria of shipment search."
            }, {
                seq: '1',
                value: 'getAdvancedShipmentListWithGiftRct_input',
                description: "The search criteria of shipment search."
            }, {
                seq: '1',
                value: 'readFromPersonInfoMarkFor',
                description: "The model used to decide target namespace in search screen."
            }, {
                seq: '1',
                value: 'IncludeOrdersPickedInOtherStore',
                description: "The model used to decide target namespace in search screen for including shipments from other stores"
            }],
            sourceBindingNamespaces: [{
                value: 'getOrderByList_output',
                description: "This is used to hold the order by drop down criteria."
            }, {
                value: 'screenInput',
                description: "This is set when the screen loads to prepopulated the search criteria."
            }, {
                value: 'selectedRow',
                description: "This is the data for the selected row in the search results."
            }, {
                value: 'IncludeOtherStores',
                description: "Include other stores - default values"
            }, {
                value: 'FirstLookFlag',
                description: "First look at default values"
            }, {
                value: 'getScacList_output',
                description: "Output of the getScacList API for carrier search criteria"
            }, {
                value: 'getShipmentList_output',
                description: "The list of organizations the user has access to find orders for."
            }, {
                value: 'getOrderByList_output',
                description: "The list of order by attributes"
            }, {
                value: 'screenInput',
                description: "Model to hold initial input data to the screen."
            }, {
                value: 'shipmentStatus',
                description: "Holds currently selected statuses"
            }, {
                value: 'getShipmentStatusList_output',
                description: "Model to hold the values for shipment status in search criteria."
            }]
        },
        staticBindings: [{
            targetBinding: {
                path: 'Shipment.ApplyQueryTimeout',
                namespace: 'getAdvancedShipmentList_input'
            },
            sourceBinding: {
                path: 'Shipment.ApplyQueryTimeout',
                sourceValue: 'Y'
            }
        }, {
            targetBinding: {
                path: 'Shipment.ShipNode',
                namespace: 'getAdvancedShipmentList_input'
            },
            sourceBinding: {
                path: 'Shipment.Store',
                namespace: 'screenInput'
            }
        }, {
            targetBinding: {
                path: 'Shipment.ShipNode',
                namespace: 'getAdvancedShipmentListWithGiftRct_input'
            },
            sourceBinding: {
                path: 'Shipment.Store',
                namespace: 'screenInput'
            }
        }],
        hotKeys: [],
        events: [{
            name: 'setSelectedRow'
        }, {
            name: 'collapseSearchResults'
        }, {
            name: 'reloadScreen'
        },  {
            name: 'reloadScreen'
        }],
        subscribers: {
            local: [
                
                {
                    eventId: 'setSelectedRow',
                    sequence: '25',
                    handler: {
                        methodName: "setSelectedRow"
                    }
                }, {
                    eventId: 'collapseSearchResults',
                    sequence: '25',
                    handler: {
                        methodName: "handleSearchResults",
                        className: "SearchUtils",
                        packageName: "ias.utils"
                    }
                }, {
                    eventId: 'afterScreenLoad',
                    sequence: '30',
                    description: 'after load',
                    handler: {
                        methodName: "initializeScreenAfterLoad"
                    }
                }, {
                    eventId: 'reloadScreen',
                    sequence: '31',
                    description: 'this event is fired after reload of this screen',
                    handler: {
                        methodName: "refreshShipmentSearch"
                    }
                }, {
                    eventId: 'afterScreenLoad',
                    sequence: '25',
                    description: 'Subscriber for after Screen Load event',
                    handler: {
                        methodName: "updateEditorHeader"
                    }
                }, {
                    eventId: 'reloadScreen',
                    sequence: '28',
                    description: 'Subscriber for reload Screen event',
                    handler: {
                        methodName: "updateEditorHeader"
                    }
                },
               
                {
                    eventId: 'txt_shipmentNo_onKeyUp',
                    sequence: '25',
                    listeningControlUId: 'txt_shipmentNo',
                    handler: {
                        methodName: "SST_invokeApiOnEnter"
                    }
                },
				{
                    eventId: 'SST_SearchButton_onClick',
                    sequence: '25',
                    description: '',
                    listeningControlUId: 'SST_SearchButton',
                    handler: {
                        methodName: "SST_invokeApi",
                        description: ""
                    }
                }],
            global: [{
                eventId: 'callMe',
                sequence: '25',
                handler: {
                    methodName: "okCalled"
                }
            }]
        }
    });
});