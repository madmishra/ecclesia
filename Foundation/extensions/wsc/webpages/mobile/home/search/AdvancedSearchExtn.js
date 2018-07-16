scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/mobile/home/search/AdvancedSearchExtnUI", "scbase/loader!wsc/mobile/home/utils/MobileHomeUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"],
	function (
		_dojodeclare,
		_extnAdvancedSearchExtnUI,
		_wscMobileHomeUtils,
		_scScreenUtils
	) {
		return _dojodeclare("extn.mobile.home.search.AdvancedSearchExtn", [_extnAdvancedSearchExtnUI], {
			// custom code here
			setShipmentStatusList: function () {
				var getShipmentStatusList_output = null;
				getShipmentStatusList_output = _wscMobileHomeUtils.getShipmentStatusListModel(
					this);
				getShipmentStatusList_output.StatusList.Status[7] = {}
				getShipmentStatusList_output.StatusList.Status[7].StatusCode = "1100.70.06.30.1";
				getShipmentStatusList_output.StatusList.Status[7].StatusKey = "Option_ReadyForPrint";
				getShipmentStatusList_output.StatusList.Status[8] = {};
				getShipmentStatusList_output.StatusList.Status[8].StatusCode = "1100.70.200";
				getShipmentStatusList_output.StatusList.Status[8].StatusKey = "Option_ReadyForGiftWrap";
				_scScreenUtils.setModel(
					this, "getShipmentStatusList_output", getShipmentStatusList_output, null);
			}
		});
	});