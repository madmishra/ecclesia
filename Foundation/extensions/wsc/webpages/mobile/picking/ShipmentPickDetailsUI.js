scDefine(["dojo/text!./templates/ShipmentPickDetails.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/IdentifierScreen", "scbase/loader!sc/plat/dojo/widgets/Image", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link"], function(
templateText, _dojodeclare, _dojokernel, _dojolang, _dojotext, _scplat, _scCurrencyDataBinder, _scImageDataBinder, _scBaseUtils, _scDataLabel, _scIdentifierScreen, _scImage, _scLabel, _scLink) {
    return _dojodeclare("extn.mobile.picking.ShipmentPickDetailsUI", [_scIdentifierScreen], {
        identifierType: "ShipmentPickDetails",
        uId: "extnShipmentPickDetails",
        packageName: "extn.mobile.picking",
        className: "ShipmentPickDetails",
        screen_description: "Screen for displaying shipment details",
		identifierList: [{
            identifierId: 'Pick',
            description: 'Create new Shipment Details with a Pickup In Store shipment to open this identifier screen'
        }, {
            identifierId: 'Ship',
            description: 'Create new Shipment Details with a Ship From Store shipment to open this identifier screen'
        }, {
            identifierId: 'Pack',
            description: 'Create new Shipment Details with a Ready for Packing shipment to open this identifier screen'
        }],
        namespaces: {
            targetBindingNamespaces: [{
                seq: '1',
                value: 'common_getShipmentDetails_input',
                description: "input model used to make common_getShipmentDetails custom mashup call."
            }, {
                seq: '1',
                value: 'shipmentSummaryWizard_input',
                description: "input model used to open shipment summary wizard"
            }],
            sourceBindingNamespaces: [{
                value: 'Shipment',
                description: "The repeating element from the output of the getShipmentList api"
            }, {
                value: 'clockImageBindingValues',
                description: "Values to be binded to the time threshold clock image"
            }]
        },
        
        staticBindings: [{
            targetBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'common_getShipmentDetails_input'
            },
            sourceBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'Shipment'
            }
        }, {
            targetBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'shipmentSummaryWizard_input'
            },
            sourceBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'Shipment'
            }
        }, {
            targetBinding: {
                path: 'Shipment.DisplayOrderNo',
                namespace: 'shipmentSummaryWizard_input'
            },
            sourceBinding: {
                path: 'Shipment.DisplayOrderNo',
                namespace: 'Shipment'
            }
        }],
        hotKeys: [],
        events: [{
            name: 'onClick',
            originatingControlUId: 'pnlIdentifierHolder'
        }],
        subscribers: {
            local: [{
                eventId: 'afterScreenInit',
                sequence: '30',
                handler: {
                    methodName: "initializeScreen"
                }
            }, {
                eventId: 'pnlIdentifierHolder_onClick',
                sequence: '30',
                description: 'This subscriber loads the ready for packing screen',
                handler: {
                    methodName: "openPickDetails"
                }
            }, {
                eventId: 'lnk_PickAction_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'lnk_PickAction',
                handler: {
                    methodName: "pickDefaultAction",
                    description: ""
                }
            }, {
                eventId: 'lnk_AssignToHoldAction_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'lnk_AssignToHoldAction',
                handler: {
                    methodName: "assignToHoldDefaultAction",
                    description: ""
                }
            }, {
                eventId: 'lnk_RecordCustomerPickupAction_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'lnk_RecordCustomerPickupAction',
                handler: {
                    methodName: "recordCustomerPickupAction",
                    description: ""
                }
            }, {
                eventId: 'lnk_ShipAction_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'lnk_ShipAction',
                handler: {
                    methodName: "shipDefaultAction",
                    description: ""
                }
            }, {
                eventId: 'lnk_PackAction_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'lnk_PackAction',
                handler: {
                    methodName: "packDefaultAction",
                    description: ""
                }
            }]
        }
    });
});