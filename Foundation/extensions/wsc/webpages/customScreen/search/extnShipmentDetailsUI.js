scDefine(["dojo/text!./templates/extnShipmentDetails.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/UIUtils", "scbase/loader!ias/widgets/IASBaseScreen", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/Image", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link"], function(
templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasUIUtils, _iasIASBaseScreen, _idxContentPane, _scplat, _scButtonDataBinder, _scCurrencyDataBinder, _scImageDataBinder, _scBaseUtils, _scScreenUtils, _scDataLabel, _scImage, _scLabel, _scLink) {
    return _dojodeclare("extn.customScreen.search.extnShipmentDetailsUI", [_iasIASBaseScreen], {
        templateString: templateText,
        uId: "shipmentShortDetails",
        packageName: "extn.customScreen.search",
        className: "extnShipmentDetails",
        title: "",
        screen_description: "shipment details screen loaded from shipment search results.",
        isDirtyCheckRequired: true,
        namespaces: {
            targetBindingNamespaces: [{
                seq: '1',
                value: 'getShipmentDetails_input',
                description: "The input model used to call getShipmentdetails from search resuls."
            }],
            sourceBindingNamespaces: [{
                value: 'pickCount_output',
                description: "Namespace populated after pickup count is determined"
            }, {
                value: 'getShipmentList_output',
                description: "Model used to save getShipmentList api output."
            }, {
                value: 'printPickTicket_output',
                description: "Output of the searchPickTicket flow"
            }, {
                value: 'clockImageBindingValues',
                description: "Values to be binded to the time threshold clock image"
            }]
        },
        staticBindings: [{
            targetBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'getShipmentDetails_input'
            },
            sourceBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'getShipmentList_output'
            }
        }, {
            targetBinding: {
                path: 'Shipment.ShipmentNo',
                namespace: 'getShipmentDetails_input'
            },
            sourceBinding: {
                path: 'Shipment.ShipmentNo',
                namespace: 'getShipmentList_output'
            }
        }],
        hotKeys: [],
        events: [],
        subscribers: {
            local: [{
                eventId: 'afterScreenInit',
                sequence: '30',
                description: 'upon initialization',
                handler: {
                    methodName: "initializeScreen"
                }
            }, {
                eventId: 'lblOrderNo_onClick',
                sequence: '30',
                description: 'User clicks on the order number',
                listeningControlUId: 'lblOrderNo',
                handler: {
                    methodName: "openShipmentDetails",
                    description: ""
                }
            }, 
			{
                eventId: 'lnkGiftWrap_onClick',
                sequence: '30',
                description: 'User clicks on the order number',
                listeningControlUId: 'lnkGiftWrap',
                handler: {
                    methodName: "openShipmentDetails",
                    description: ""
                }
            },
            // {
            //     eventId: 'lnkEmail_onClick',
            //     sequence: '30',
            //     description: 'User clicks on the email id',
            //     listeningControlUId: 'lnkEmail',
            //     handler: {
            //         methodName: "openEmailForLink",
            //         className: "UIUtils",
            //         packageName: "ias.utils",
            //         description: ""
            //     }
            // }, {
            //     eventId: 'lnkStartBRP_onClick',
            //     sequence: '30',
            //     description: 'User clicks on start backroom pick',
            //     listeningControlUId: 'lnkStartBRP',
            //     handler: {
            //         methodName: "startBackroomPick",
            //         description: ""
            //     }
            // }, {
            //     eventId: 'lnkPrint_onClick',
            //     sequence: '30',
            //     description: 'User lciks on print',
            //     listeningControlUId: 'lnkPrint',
            //     handler: {
            //         methodName: "handlePickTicket"
            //     }
            // }, {
            //     eventId: 'lnkContinueBRP_onClick',
            //     sequence: '30',
            //     description: 'User clicks on continue backroom pick',
            //     listeningControlUId: 'lnkContinueBRP',
            //     handler: {
            //         methodName: "continueBackroomPick",
            //         description: ""
            //     }
            // }, {
            //     eventId: 'lnkStartCustomerPickup_onClick',
            //     sequence: '30',
            //     description: 'User clicks on start customer pickup',
            //     listeningControlUId: 'lnkStartCustomerPickup',
            //     handler: {
            //         methodName: "startCustomerPick",
            //         description: ""
            //     }
            // }, {
            //     eventId: 'lnkPack_onClick',
            //     sequence: '30',
            //     description: 'User clicks on start pack',
            //     listeningControlUId: 'lnkPack',
            //     handler: {
            //         methodName: "startPack",
            //         description: ""
            //     }
            // }, {
            //     eventId: 'lnkContinuePack_onClick',
            //     sequence: '30',
            //     description: 'User clicks on continue pack',
            //     listeningControlUId: 'lnkContinuePack',
            //     handler: {
            //         methodName: "continuePack",
            //         description: ""
            //     }
            // }
        ]
        }
        // ,
        // handleMashupOutput: function(
        // mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
        //     if (
        //     _scBaseUtils.equals(
        //     mashupRefId, "getShipmentDetailsForBackroomPick")) {
        //         this.handle_getShipmentDetailsForBackroomPick(
        //         mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        //     }
        //     if (
        //     _scBaseUtils.equals(
        //     mashupRefId, "getShipmentDetailsForPack")) {
        //         this.handle_getShipmentDetailsForPack(
        //         mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        //     }
        //     if (
        //     _scBaseUtils.equals(
        //     mashupRefId, "containerPack_changeShipment")) {
        //         this.handle_containerPack_changeShipment(
        //         mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        //     }
        //     if (
        //     _scBaseUtils.equals(
        //     mashupRefId, "getShipmentDetailsForRecordCustomerPick")) {
        //         this.handle_getShipmentDetailsForRecordCustomerPick(
        //         mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        //     }
        //     if (
        //     _scBaseUtils.equals(
        //     mashupRefId, "changeShipmentToUpdateQty")) {
        //         if (!(
        //         _scBaseUtils.equals(
        //         false, applySetModel))) {
        //             _scScreenUtils.setModel(
        //             this, "pickCount_output", modelOutput, null);
        //         }
        //         this.handle_changeShipmentToUpdateQty(
        //         mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        //     }
        //     if (
        //     _scBaseUtils.equals(
        //     mashupRefId, "searchPickTicket")) {
        //         if (!(
        //         _scBaseUtils.equals(
        //         false, applySetModel))) {
        //             _scScreenUtils.setModel(
        //             this, "printPickTicket_output", modelOutput, null);
        //         }
        //         this.handle_searchPickTicket(
        //         mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        //     }
        // }
    });
});