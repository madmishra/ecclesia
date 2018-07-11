scDefine(["dojo/text!./templates/SortScanPick.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!ias/widgets/IASBaseScreen", "scbase/loader!idx/form/TextBox", "scbase/loader!idx/layout/ContentPane", "scbase/loader!idx/layout/TitlePane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/binding/SimpleDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/widgets/ControllerWidget", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget", "scbase/loader!sc/plat/dojo/widgets/Image", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link", "scbase/loader!wsc/components/shipment/common/screens/LastScannedProductDetailsInitController", "scbase/loader!extn/customScreen/sort/SortLineDetails"], function(
templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasRepeatingScreenUtils, _iasScreenUtils, _iasIASBaseScreen, _idxTextBox, _idxContentPane, _idxTitlePane, _scplat, _scButtonDataBinder, _scCurrencyDataBinder, _scImageDataBinder, _scSimpleDataBinder, _scBaseUtils, _scScreenUtils, _scControllerWidget, _scDataLabel, _scIdentifierControllerWidget, _scImage, _scLabel, _scLink, _wscLastScannedProductDetailsInitController, _extnSortLineDetails) {
    return _dojodeclare("extn.customScreen.sort.SortScanPickUI", [_iasIASBaseScreen], {
        templateString: templateText,
        uId: "SortScanPick",
        packageName: "extn.customScreen.sort.SortScanPick",
        className: "SortScanPick",
        title: "Title_SortScanPick",
        screen_description: "Item scan screen for Backroom Pick",
        lastScannedItemShipmentLineKey: '',
        nextView: '',
        screenReloaded: false,
        onlyShortageLinesDisplayed: false,
        activeRepeatingPanelUId: '',
        isDirtyCheckRequired: true,
        currentView: 'shortItems',
        flowName: 'BackroomPick',
        backroomPickSaved: false,
        isInitViewPainted: false,
        LastScannedShipmentLineScreenUId: '',
        namespaces: {
            targetBindingNamespaces: [{
                value: 'translateBarCode_input',
                description: "This is the input from the screen to launch the next screen."
            }, {
                value: 'updateShipmentPickQuantity_input',
                description: "This is the input from the screen to launch the next screen."
            }],
            sourceBindingNamespaces: [{
                value: 'backroomPickShipmentLineList_output',
                description: "This namespace contains shipment line list model"
            }, {
                value: 'backroomPickShipmentDetails_output',
                description: "This namespace contains shipment details model"
            }, {
                value: 'translateBarCode_output',
                description: "This namespace contains scanned barcode model"
            }, {
                value: 'lastProductScanned_output',
                description: "This namespace contains last scanned product model"
            }, {
                value: 'clockImageBindingValues',
                description: "Values to be binded to the time threshold clock image"
            }, {
                value: 'getShortageLines',
                description: "This namespace contains to be picked shipment line list model"
            }, {
                value: 'getReadyForPickLines',
                description: "This namespace contains all the shipment lines model of the shipment"
            }]
        },
        staticBindings: [{
            targetBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'updateShipmentPickQuantity_input'
            },
            sourceBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'backroomPickShipmentDetails_output'
            }
        }, {
            targetBinding: {
                path: 'BarCode.ShipmentContextualInfo.ShipmentKey',
                namespace: 'translateBarCode_input'
            },
            sourceBinding: {
                path: 'Shipment.ShipmentKey',
                namespace: 'backroomPickShipmentDetails_output'
            }
        }, {
            targetBinding: {
                path: 'BarCode.ShipmentContextualInfo.SellerOrganizationCode',
                namespace: 'translateBarCode_input'
            },
            sourceBinding: {
                path: 'Shipment.SellerOrganizationCode',
                namespace: 'backroomPickShipmentDetails_output'
            }
        }, {
            targetBinding: {
                path: 'BarCode.ContextualInfo.EnterpriseCode',
                namespace: 'translateBarCode_input'
            },
            sourceBinding: {
                path: 'Shipment.EnterpriseCode',
                namespace: 'backroomPickShipmentDetails_output'
            }
        }],
        hotKeys: [],
        events: [{
            name: 'showHideAbandonBPPopUp'
        }, {
            name: 'handleReloadScreenEvent'
        }, {
            name: 'updateShipmentLineDetails'
        }, {
            name: 'allItems_onShow'
        }, {
            name: 'shortItems_onShow'
        }, {
            name: 'allItems_beforeHide'
        }, {
            name: 'shortItems_beforeHide'
        }, {
            name: 'loadSelectView'
        }, {
            name: 'reloadSelectView'
        }, {
            name: 'saveCurrentPage'
        }, {
            name: 'reloadScreen'
        }, {
            name: 'beforePreviousPage'
        }],
        subscribers: {
            local: [{
                eventId: 'afterScreenLoad',
                sequence: '35',
                description: 'This method is called on after screen has loaded',
                handler: {
                    methodName: "afterScreenLoad"
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '25',
                description: 'This method is used to perform screen initialization tasks.',
                handler: {
                    methodName: "initializeScreen"
                }
            }, {
                eventId: 'updateShipmentLineDetails',
                sequence: '30',
                description: 'This method is used to handle Key Up event of Scan Product text field.',
                handler: {
                    methodName: "updateShipmentLineDetails"
                }
            }, {
                eventId: 'scanProductIdTxt_onKeyUp',
                sequence: '30',
                description: 'This method is used to handle Key Up event of Scan Product text field.',
                handler: {
                    methodName: "scanProductOnEnter"
                }
            }, {
                eventId: 'handleReloadScreenEvent',
                sequence: '30',
                description: 'this event is fired after reload of this screen',
                handler: {
                    methodName: "handleReloadScreen"
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
                eventId: 'viewShortItemsLink_onClick',
                sequence: '30',
                description: 'Subscriber for View Short Items link.',
                listeningControlUId: 'viewShortItemsLink',
                handler: {
                    methodName: "displayShortItems",
                    description: "handled for displaying shortage items"
                }
            }, {
                eventId: 'pickAll_onClick',
                sequence: '30',
                description: 'Subscriber for onclick action of Pick All Button',
                listeningControlUId: 'pickAll',
                handler: {
                    methodName: "pickAll",
                    description: "Handler for Pick All"
                }
            }, {
                eventId: 'repeatingIdentifierScreen_afterPagingLoad',
                sequence: '25',
                description: 'This event is triggered to call the after page load',
                handler: {
                    methodName: "hideNavigationForSinglePageResult",
                    className: "RepeatingScreenUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'readyForPickupLineList_afterPagingLoad',
                sequence: '25',
                description: 'This event is triggered to call the after page load',
                listeningControlUId: 'readyForPickupLineList',
                handler: {
                    methodName: "hideNavigationForSinglePageResult",
                    className: "RepeatingScreenUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'unpickedLineList_afterPagingLoad',
                sequence: '25',
                description: 'This event is triggered to call the after page load',
                listeningControlUId: 'unpickedLineList',
                handler: {
                    methodName: "hideNavigationForSinglePageResult",
                    className: "RepeatingScreenUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '26',
                description: 'After screen init to show default selected view',
                handler: {
                    methodName: "initSelectedView",
                    className: "ScreenUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'shortItems_onShow',
                sequence: '26',
                description: 'Show unpicked items',
                listeningControlUId: 'shortItems_ContentPane',
                handler: {
                    methodName: "shortItems_onShow"
                }
            }, {
                eventId: 'allItems_onShow',
                sequence: '26',
                description: 'Show all items',
                listeningControlUId: 'allItems_ContentPane',
                handler: {
                    methodName: "allItems_onShow"
                }
            }, {
                eventId: 'shortItems_beforeHide',
                sequence: '26',
                description: 'Hide unpicked items',
                listeningControlUId: 'shortItems_ContentPane',
                handler: {
                    methodName: "shortItems_beforeHide"
                }
            }, {
                eventId: 'allItems_beforeHide',
                sequence: '26',
                description: 'Hide all items',
                listeningControlUId: 'allItems_ContentPane',
                handler: {
                    methodName: "allItems_beforeHide"
                }
            }, {
                eventId: 'loadSelectView',
                sequence: '26',
                description: 'Load Selected view',
                handler: {
                    methodName: "loadSelectedView",
                    className: "ScreenUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'reloadSelectView',
                sequence: '26',
                description: 'reload Selected view',
                handler: {
                    methodName: "reloadSelectedView",
                    className: "ScreenUtils",
                    packageName: "ias.utils"
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
            }, {
                eventId: 'saveCurrentPage',
                sequence: '25',
                description: 'Subscriber for save current page event for wizard',
                handler: {
                    methodName: "save"
                }
            }, {
                eventId: 'reloadScreen',
                sequence: '25',
                description: 'Subscriber for reload Screen event',
                handler: {
                    methodName: "handleReloadScreen"
                }
            }, {
                eventId: 'beforePreviousPage',
                sequence: '25',
                description: 'Subscriber for beforePreviousPage event for wizard',
                handler: {
                    methodName: "handleBeforePrevious"
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
            }, {
                eventId: 'shortItems_onClick',
                sequence: '30',
                description: 'Subscriber for event raised on clicking view all lines tab',
                listeningControlUId: 'shortItems',
                handler: {
                    methodName: "changeView",
                    className: "ScreenUtils",
                    packageName: "ias.utils"
                }
            }, {
                eventId: 'allItems_onClick',
                sequence: '30',
                description: 'Subscriber for event raised on clicking view all lines tab',
                listeningControlUId: 'allItems',
                handler: {
                    methodName: "changeView",
                    className: "ScreenUtils",
                    packageName: "ias.utils"
                }
            },
			{
                eventId: 'moveAhead',
                sequence: '26',
                description: 'reload Selected view',
                handler: {
                    methodName: "moveAhead"
                }
            },]
        },
        handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "getNotPickedShipmentLineListCount")) {
                this.showHideShipmentLineList(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "validateChangeShipmentStatusOnNext")) {
                this.canProceedToNextScreen(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "saveShipmentStatusForPickUpOrder")) {
                this.gotoNextScreen();
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "saveShipmentStatusForShipOrder")) {
                this.gotoNextScreen();
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getNotPickedShipmentLineListOnNext")) {
                this.isPickComplete(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getAllShipmentLineList")) {
                this.updateShipmentLineListPanel(
                modelOutput, "ALLLINES");
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getNotPickedShipmentLineList")) {
                this.updateShipmentLineListPanel(
                modelOutput, "SHORTLINES");
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "updateShipmentQuantityForPickAllLine")) {
                this.refreshShipmentLineAfterQuantityUpdate(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "updateShipmentQuantityForPickAll")) {
                this.showConfirmMsgBoxAfterPickAll(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "translateBarCode")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "translateBarCode_output", modelOutput, null);
                }
                this.updateProductQuantity(
                modelOutput);
            }
        }
    });
});