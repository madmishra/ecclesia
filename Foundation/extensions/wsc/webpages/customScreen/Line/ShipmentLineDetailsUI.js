scDefine(["dojo/text!./templates/ShipmentLineDetails.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!ias/widgets/IASBaseScreen", "scbase/loader!idx/form/NumberTextBox", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/binding/SimpleDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/Image", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link", "scbase/loader!ias/utils/UIUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils"], function (
    templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasScreenUtils, _iasIASBaseScreen, _idxNumberTextBox, _idxContentPane, _scplat, _scButtonDataBinder, _scCurrencyDataBinder, _scImageDataBinder, _scSimpleDataBinder, _scBaseUtils, _scDataLabel, _scImage, _scLabel, _scLink, _iasUIUtils,_scScreenUtils) {
    return _dojodeclare("extn.customScreen.Line.ShipmentLineDetailsUI", [_iasIASBaseScreen], {
        templateString: templateText,
        uId: "extn_shipmentLineDetails",
        packageName: "extn.customScreen.Line",
        className: "ShipmentLineDetails",
        title: "Title_ShipmentLineDetails",
        screen_description: "Screen for displaying shipment line",
        isDirtyCheckRequired: true,
        flowName: 'BackroomPick',
        pickedQuantity: '0',
        shortageResolved: false,
        namespaces: {
            targetBindingNamespaces: [{
                value: 'ShipmentLine_Output',
                description: "This namespace contains the shipment line details"
            }, {
                value: 'ShipmentLineKey_Output',
                description: "This namespace contains the shipment line details"
            }, {
                value: 'ShipmentLineQty',
                description: "This namespace contains the shipment line details"
            }, {
                value: 'Shipment_Output',
                description: "This namespace contains the shipment line details"
            }, {
                value: 'PickAll_Output',
                description: "This namespace contains scanned quantity of selected shipment lines."
            }, {
                value: 'ShipmentKey_Output',
                description: "This namespace contains scanned quantity of selected shipment lines."
            }, {
                value: 'ItemDetails',
                description: "This namespace contains item details required for launching product details screen"
            }, {
                value: 'QuantityTextBoxModel_Output',
                description: "This namespace contains picked qunatity value binded to Quantity text box."
            }, {
                value: 'Shortage_Output',
                description: "This model is used to pass shipment information to shortage popups/screen "
            }],
            sourceBindingNamespaces: [{
                value: 'ShipmentLine',
                description: "This namespace contains Shipment Line details."
            }, {
                value: 'ShortedShipmentLine',
                description: "This namespace contains Shorted Shipment Line details."
            }, {
                value: 'QuantityTextBoxModel_Input',
                description: "This namespace contains picked quantity value of Shipment line"
            }, {
                value: 'QuantityReadOnlyModel_Input',
                description: "This namespace contains picked quantity value of Shipment line when user cannot manually update the picked qunatity."
            }, {
                value: 'ShortedShipmentLineModel',
                description: "This namespace contains Shorted Shipment Line details"
            }, {
                value: 'Shipment',
                description: "This namespace is used to hold Shipment Details model"
            }]
        },
        staticBindings: [{
            targetBinding: {
                path: 'ShipmentLine.ShipmentKey',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShipmentKey',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.ShipmentLineKey',
                namespace: 'ShipmentLineKey_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShipmentLineKey',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'ShipmentKey_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShipmentKey',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'Shipment_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShipmentKey',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.ItemID',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.ItemID',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.DisplayUnitOfMeasure',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.DisplayUnitOfMeasure',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.UOMDisplayFormat',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.UOMDisplayFormat',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.UnitOfMeasure',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.UnitOfMeasure',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.Item.PorductClass',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.Item.PorductClass',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ImageID',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ImageID',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ImageLabel',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ImageLabel',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ImageLocation',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ImageLocation',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.ShipmentLineKey',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShipmentLineKey',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.ShipmentLineNo',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShipmentLineNo',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.ShipmentSubLineNo',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShipmentSubLineNo',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.BackroomPickedQuantity',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'Quantity',
                namespace: 'QuantityTextBoxModel_Input'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderHeaderKey',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderHeaderKey',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLineKey',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLineKey',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.Quantity',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.Quantity',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'Quantity',
                namespace: 'PickAll_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.Quantity',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.ShortageQty',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShortageQty',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.AttributeList',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.AttributeList',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShortageQty',
                namespace: 'PickAll_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.ShortageQty',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ExtendedDisplayDescription',
                namespace: 'ShipmentLine_Output'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ExtendedDisplayDescription',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'ShipmentLine.Quantity',
                namespace: 'ShipmentLineQty'
            },
            sourceBinding: {
                path: 'ShipmentLine.Quantity',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'Item.ItemID',
                namespace: 'ItemDetails'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.ItemID',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'Item.UnitOfMeasure',
                namespace: 'ItemDetails'
            },
            sourceBinding: {
                path: 'ShipmentLine.OrderLine.ItemDetails.UnitOfMeasure',
                namespace: 'ShipmentLine'
            }
        }, {
            targetBinding: {
                path: 'Item.CallingOrganizationCode',
                namespace: 'ItemDetails'
            },
            sourceBinding: {
                path: 'Shipment.EnterpriseCode',
                namespace: 'Shipment'
            }
        }, {
            targetBinding: {
                path: 'CommonCode.CallingOrganizationCode',
                namespace: 'Shortage_Output'
            },
            sourceBinding: {
                path: 'Shipment.EnterpriseCode',
                namespace: 'Shipment'
            }
        }],
        hotKeys: [],
        events: [{
            name: 'updateScannedProductQuantity'
        }, {
            name: 'handleQuantityChange'
        }, {
            name: 'saveCurrentPage'
        }, {
            name: 'reloadScreen'
        }, {
            name: 'beforePreviousPage'
        }],
        subscribers: {
            local: [{
                eventId: 'afterScreenInit',
                sequence: '25',
                description: 'This methods contains the screen initialization tasks.',
                handler: {
                    methodName: "initializeScreen"
                }
            },

            // {
            //     eventId: 'pickAllLink_onClick',
            //     sequence: '30',
            //     description: 'This methods pciks all the quantity of shipment line.',
            //     listeningControlUId: 'pickAllLink',
            //     handler: {
            //         methodName: "pickAllQuantity",
            //         description: ""
            //     }
            // }, 
            // {
            //     eventId: 'updateScannedProductQuantity',
            //     sequence: '25',
            //     description: 'This event is used to update quantity of scanned product.',
            //     handler: {
            //         methodName: "updateScannedProductQuantity"
            //     }
            // },
            //  {
            //     eventId: 'txtScannedQuantity_onKeyUp',
            //     sequence: '30',
            //     description: 'This method is used to handle Key Up event of Scan Product text field.',
            //     handler: {
            //         methodName: "editQuantityOnEnter"
            //     }
            // }, 
            {
                eventId: 'itemdescriptionLink_onClick',
                sequence: '30',
                description: 'This method is used to open product details screen.',
                handler: {
                    methodName: "openItemDetails"
                }
            },
            //  {
            //     eventId: 'shortageResolutionLink_onClick',
            //     sequence: '30',
            //     description: 'This method is used to open shortage resolution popup',
            //     listeningControlUId: 'shortageResolutionLink',
            //     handler: {
            //         methodName: "openShortageResolutionPopup",
            //         description: ""
            //     }
            // },
            //  {
            //     eventId: 'updateQtyButton_onClick',
            //     sequence: '30',
            //     description: 'Subscriber for onclick action of Pick All Button',
            //     listeningControlUId: 'updateQtyButton',
            //     handler: {
            //         methodName: "updatePickedQuantity",
            //         description: "Handler for Update Action"
            //     }
            // }, {
            //     eventId: 'handleQuantityChange',
            //     sequence: '30',
            //     description: 'This method is used to update shipment line panel with quantity updates',
            //     handler: {
            //         methodName: "handleQuantityChange"
            //     }
            // }, {
            //     eventId: 'itemImage_imageClick',
            //     sequence: '30',
            //     description: 'This method is used to open product details screen.',
            //     handler: {
            //         methodName: "openItemDetails"
            //     }
            // },
            {
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
            }, {
                eventId: 'saveCurrentPage',
                sequence: '25',
                description: 'Subscriber for save current page event for wizard',
                handler: {
                    methodName: "save"
                }
            }, {
                eventId: 'beforePreviousPage',
                sequence: '25',
                description: 'Subscriber for beforePreviousPage event for wizard',
                handler: {
                    methodName: "handleBeforePrevious"
                }
            }, {
                eventId: 'itemImage_onLoad',
                sequence: '30',
                description: 'This method is used to open product details screen.',
                listeningControlUId: 'itemImage',
                handler: {
                    methodName: "resetProductAspectRatio",
                    className: "ScreenUtils",
                    packageName: "ias.utils"
                }
            },
            {
                eventId: 'giftWrapAction',
                sequence: '30',
                description: '',
                handler: {
                    methodName: "giftWrapAction"
                }
            }
                //, 
                // {
                //     eventId: 'removeQtyLink_onClick',
                //     sequence: '30',
                //     description: 'This method reduces the picked quantity by 1',
                //     listeningControlUId: 'removeQtyLink',
                //     handler: {
                //         methodName: "decreaseQuantity"
                //     }
                // },
                //  {
                //     eventId: 'addQtyLink_onClick',
                //     sequence: '30',
                //     description: 'This method increase the picked quantity by 1',
                //     listeningControlUId: 'addQtyLink',
                //     handler: {
                //         methodName: "increaseQuantity"
                //     }
                // }
            ]
        }
    });
});