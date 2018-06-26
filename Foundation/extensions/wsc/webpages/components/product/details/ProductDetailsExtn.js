scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/components/product/details/ProductDetailsExtnUI"],
	function (
		_dojodeclare, _scScreenUtils,
		_extnProductDetailsExtnUI
	) {


		return _dojodeclare("extn.components.product.details.ProductDetailsExtn", [_extnProductDetailsExtnUI], {
			// custom code here
			updateAuthor: function () {
				batchModel = _scScreenUtils.getModel(this, "itemDetailsModel");
				console.log('working from mob');
				var Subject = "";
				var Author = "";
				var Series = "";
				var Brand = "";
				var additionalAtrributeArray = batchModel.Item.AdditionalAttributeList.AdditionalAttribute
				for (var i = 0; i < additionalAtrributeArray.length; i++) {
					if (additionalAtrributeArray[i].Name === "Subject") {
						Subject = additionalAtrributeArray[i].Value;
					}
					if (additionalAtrributeArray[i].Name === "Author") {
						Author = additionalAtrributeArray[i].Value;
					}
					if (additionalAtrributeArray[i].Name === "Series") {
						Series = additionalAtrributeArray[i].Value;
					}
					if (additionalAtrributeArray[i].Name === "Brand") {
						Brand = additionalAtrributeArray[i].Value;
					}
				}
				_scScreenUtils.setModel(this, "extn_detailsModel", {
					Brand: Brand,
					Subject: Subject,
					Author: Author,
					Series: Series
				}, null)
			}
		});
	});