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

				var shipmentLineArray = batchModel.Shipment.ShipmentLines.ShipmentLine;
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
					// _scWidgetUtils.showWidget(
					// 	this, "extn_button", true, null);
					this.changePrintStatus();
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
			callLegacyMessage: function (modelOutput) {
				// console.log('modelOutput' , modelOutput); 
				_iasUIUtils.callApi(this, modelOutput, "extn_callLegacyMessage", null);
			},
			printDetails: function (secondaryDetails) {
				var batchModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				var printModel = {};
				if (batchModel.Shipment) {
					if (batchModel.Shipment.BillToAddress) {
						printModel.PrimaryFirstName = batchModel.Shipment.BillToAddress.FirstName;
						printModel.PrimarySecondName = batchModel.Shipment.BillToAddress.LastName;
					}
					printModel.OrderNo = batchModel.Shipment.DisplayOrderNo;
					printModel.PickupDate = new Date(batchModel.Shipment.ExpectedShipmentDate);
				}
				if (secondaryDetails.Order.OrderLines && secondaryDetails.Order.OrderLines.OrderLine) {
					var tempArray = secondaryDetails.Order.OrderLines.OrderLine;
					if (tempArray && tempArray[0].AdditionalAddresses && tempArray[0].AdditionalAddresses.AdditionalAddress[0] && tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo) {
						var firstName = tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo.FirstName;
						var secondName = tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo.LastName;
						printModel.SecondaryFirstName = firstName;
						printModel.SecondaryLastName = secondName;
					}
				}
				console.log('Invoking print with the following data - ', printModel);
				if (window.webkit) {
					window.webkit.messageHandlers.invokePrint.postMessage(printModel);
				}
			},
			printInvokeEvent: function () {
				var batchModel = _scScreenUtils.getModel(this, "getShipmentDetails_output");
				_iasUIUtils.callApi(this, {
					Order: {
						OrderNo: batchModel.Shipment.DisplayOrderNo
					}
				}, "extn_getPrintDetails", null);
			},
			updateSecondaryContact: function (event, bEvent, ctrl, args) {
				console.log('event', event.SecondaryPerson);
				var orderLineKeyArray = [];
				var orderHeaderKey = "";
				var changeOrderInput = {
					Order: {
						OrderLines: {
							OrderLine: []
						}
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
				console.log('orderLineKeyArray', orderLineKeyArray);
				changeOrderInput.Order.OrderHeaderKey = orderHeaderKey;
				for (var i = 0; i < orderLineKeyArray.length; i++) {
					var tempOrderLines = {
						OrderLineKey: orderLineKeyArray[i],
						AdditionalAddresses: [{
							AdditionalAddress: {
								AddressType: "AlternatePickupPerson",
								PersonInfo: {
									AddressID: "AlternatePickupPerson",
									FirstName: event.SecondaryPerson,
									LastName: event.LastName
								}
							}
						}]
					};
					changeOrderInput.Order.OrderLines.OrderLine.push(tempOrderLines);
				}
				console.log('changeOrderInput', changeOrderInput);
				_iasUIUtils.callApi(this, changeOrderInput, "extn_addSecondaryContact", null);

			}
		});
	});