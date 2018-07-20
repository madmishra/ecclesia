


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/components/batch/batchpick/scanlist/BatchLineDetailsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnBatchLineDetailsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.components.batch.batchpick.scanlist.BatchLineDetailsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.components.batch.batchpick.scanlist.BatchLineDetailsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_getItemDetails'
,
		 mashupId : 			'extn_getItemDetailsForPick'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

