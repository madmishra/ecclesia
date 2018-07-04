scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/dom", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!extn/components/shipment/summary/ShipmentSummaryExtnUI", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils"],
	function (
		_dojodeclare, _dojodom, _scEventUtils, _scWidgetUtils, _scScreenUtils, _scWizardUtils, _iasUIUtils,
		_extnShipmentSummaryExtnUI, _iasUIUtils, _scEditorUtils
	) {
		return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryExtn", [_extnShipmentSummaryExtnUI], {
			// custom code here
			changePrintStatus: function () {
				var batchModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				var baseDropStatus = "1100.70.300";
				debugger
				var shipmentLineArray = batchModel.Shipment.ShipmentLines.ShipmentLine
				for (var i = 0; i < shipmentLineArray.length; i++)
					if (shipmentLineArray[i].OrderLine.GiftWrap === "Y") {
						baseDropStatus = "1100.70.200";
						break;
					}
				var changeShipmentInput = {
					Shipment: {
						ShipmentKey: batchModel.Shipment.ShipmentKey,
						BaseDropStatus: baseDropStatus,
						TransactionId: "PRINT_ORDER_SLIP.0001.ex"
					}
				};
				_iasUIUtils.callApi(this, changeShipmentInput, "extn_printStatus", null);
			},
			printButtonCheck: function () {
				var batchModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				if (batchModel.Shipment.Status.Status === "1100.70.06.30.1")
					_scWidgetUtils.showWidget(
						this, "extn_button", true, null);
			},
			imGettingCalled: function () {
				//console.log('im here');
			},
			populateChildEvent: function () {
				console.log('im in populateChildEvent');
				_scEventUtils.fireEventToChild(this, "ShipmentSummaryShipmentLineDetails", "doThisForMe", null);
			},
			formateShipdate: function (dataValue, screen, widget, nameSpace, inboxModel) {
				return new Date(dataValue);
			},
			getData: function (event, bEvent, ctrl, args) {

				// var wizardInstance = _iasUIUtils.getWizardForScreen(this);
				// var nextScreen = _wizardUtils.getCurrentPage(wizardInstance);
				// console.log('nextScreen', nextScreen);
				// console.log('wizardInstance', wizardInstance);

				//	console.log('this ', this);
				batchModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				//console.log('batchModel', batchModel);
				var node = document.createElement("LI");
				var textnode = document.createTextNode(batchModel.Shipment.ShipNode.ShipNode);
				node.appendChild(textnode);
				var textnode = document.createTextNode(batchModel.Shipment.ShipmentNo);
				node.appendChild(textnode);
				document.getElementById("hamburgerPanelId").appendChild(node);
			},
			refreshPage: function () {
				var wizInstance = _iasUIUtils.getWizardForScreen(this);
				_scWizardUtils.closeWizard(wizInstance);
			},
			continueGiftWrap: function () {
				var giftWrap = "extn_button_gift_wrap";
				var self = this;
				var wizardInstance = _iasUIUtils.getWizardForScreen(this);
				//console.log('wizardInstance', wizardInstance);
				giftwrapModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");

				console.log('getShipmentDetails_output', giftwrapModel);
				_scWidgetUtils.hideWidget(self, giftWrap, true);
				if (giftwrapModel.Shipment && giftwrapModel.Shipment.Status) {
					var status = giftwrapModel.Shipment.Status.Status;
					//console.log('inside first if');
					if (status === "1100.70.200") {
						//	console.log('inside seoncd if');
						_scWidgetUtils.showWidget(self, giftWrap, true, null);
					}
				}
			},

			openConfirmationPopup: function () {

				var targetModel = null;
				targetModel = _scScreenUtils.getTargetModel(
					this, "getShipmentDetails_output", null);
				var batchModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				_iasUIUtils.openWizardInEditor("extn.wizards.BackroomPickupWizard", batchModel, "wsc.desktop.editors.ShipmentEditor", this, null);
			},

			updateSecondaryContact: function (event, bEvent, ctrl, args) {
				console.log('event', event.SecondaryPerson);
				var orderLineKeyArray = [];
				var orderHeaderKey = "";
				var changeOrderInput = {
					Order: {
						OrderLines: []
					}
				};
				var rtModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				if (rtModel && rtModel.Shipment && rtModel.Shipment.ShipmentLines && rtModel.Shipment.ShipmentLines.ShipmentLine) {
					for (var i = 0; i < rtModel.Shipment.ShipmentLines.ShipmentLine.length; i++) {
						var shipmentLineArray = rtModel.Shipment.ShipmentLines.ShipmentLine[i];
						var orderLineKey = shipmentLineArray.OrderLine.OrderLineKey;
						orderLineKeyArray.push(orderLineKey);
					}
					orderHeaderKey = rtModel.Shipment.ShipmentLines.ShipmentLine[0].OrderHeaderKey;
				}
				changeOrderInput.Order.OrderHeaderKey = orderHeaderKey;
				for (var i = 0; i < orderLineKeyArray.length; i++) {
					var tempOrderLines = {
						OrderLine: {
							OrderLineKey: orderLineKeyArray[i],
							AdditionalAddresses: [{
								AdditionalAddress: {
									AddressType: "AlternatePickupPerson",
									PersonInfo: {
										AddressID: event.SecondaryPerson
									}
								}
							}]
						}
					};
					changeOrderInput.Order.OrderLines.push(tempOrderLines);
				}
				_iasUIUtils.callApi(this, changeOrderInput, "extn_addSecondaryContact", null);
			}
		});
	});