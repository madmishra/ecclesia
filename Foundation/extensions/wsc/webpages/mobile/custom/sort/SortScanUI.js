scDefine(["dojo/text!./templates/SortScan.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/widgets/IASBaseScreen", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link", "scbase/loader!ias/utils/UIUtils", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!wsc/mobile/home/utils/MobileHomeUtils", "scbase/loader!sc/plat/dojo/Userprefs"], function (
    templateText, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasRepeatingScreenUtils, _iasIASBaseScreen, _idxContentPane, _scplat, _scCurrencyDataBinder, _scBaseUtils, _scLabel, _scLink, _iasUIUtils, _iasScreenUtils, _wscMobileHomeUtils, _scUserprefs) {
    return _dojodeclare("extn.mobile.custom.sort.SortScanUI", [_iasIASBaseScreen], {
        templateString: templateText,
        uId: "SortScan",
        packageName: "extn.mobile.custom.sort",
        className: "SortScan",
        title: "Title_Sort",
        namespaces: {
            targetBindingNamespaces: [{
                    value: 'getShipmentSearch_input',
                    description: "The search criteria of shipment search.."
                }, {
                    value: 'IncludeOrdersPickedInOtherStore',
                    description: "The search criteria of shipment search."
                },
                {
                    value: 'translateBarCode_output',
                    description: "This namespace contains scanned barcode model"
                }
            ],
            sourceBindingNamespaces: [{
                    value: 'getShipmentList_output',
                    description: "The list of organizations the user has access to find orders for."
                },
                {
                    value: 'extn_mobile_getShipmentList_output',
                    description: "The list of organizations the user has access to find orders for."
                }
            ]
        },
        events: [],
        subscribers: {
            local: [{
                    eventId: 'afterScreenInit',
                    sequence: '25',
                    description: 'This method is used to perform screen initialization tasks.',
                    handler: {
                        methodName: "initializeScreen"
                    }
                }, {
                    eventId: 'scanProductIdTxt_onKeyUp',
                    sequence: '30',
                    description: 'This method is used to handle Key Up event of Scan Product text field.',
                    handler: {
                        methodName: "scanProductOnEnter"
                    }
                }, {
                    eventId: 'addProductButton_onClick',
                    sequence: '30',
                    description: 'Subscriber for Scan/Add button',
                    listeningControlUId: 'addProductButton',
                    handler: {
                        methodName: "scanProduct",
                        description: "Handled for Scan product"
                    }
                }, {
                    eventId: 'scanButton_onClick',
                    sequence: '30',
                    description: 'This method is used to scan a product on click of scan button',
                    listeningControlUId: 'addProductButton',
                    handler: {
                        methodName: "scanProduct",
                        description: ""
                    }
                },
                {
                    eventId: 'scanProductIdTxt_onBlur',
                    sequence: '31',
                    description: 'This method is used to scan a product on click of scan button',
                    listeningControlUId: 'addProductButton',
                    handler: {
                        methodName: "scanProduct",
                        description: ""
                    }
                }
            ]
        },
        handleMashupOutput: function (
            mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            console.log('modelOutput', modelOutput);
            if (
                _scBaseUtils.equals(
                    mashupRefId, "translateBarCode")) {
                if (!(
                        _scBaseUtils.equals(
                            false, applySetModel))) {
                    _scScreenUtils.setModel(
                        this, "translateBarCode_output", modelOutput, null);
                }
            }
            if (
                mashupRefId === "extn_getDetails") {}
            this.handleSort(modelOutput);
        }
        // checkForSort: function (modelOutput) {
        //     var size = modelOutput.ShipmentLines.ShipmentLine.Shipment.length;

        //     for (var i = 0; i <= size; i++) {
        //         if(modelOutput.ShipmentLines.ShipmentLine[0].Shipment[i].PackListType !== "Sort"){
        //             return modelOutput.ShipmentLines.ShipmentLine[0].ShipmentKey;
        //         }
        //         else 
        //             return "";
        //     }
        // }
    });
});