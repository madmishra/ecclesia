


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/mobile/home/subscreens/TasksInProgressExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnTasksInProgressExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.mobile.home.subscreens.TasksInProgressExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.mobile.home.subscreens.TasksInProgressExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getShipmentList'
,
		 sequence : 			''
,
		 mashupId : 			'extn_tasksInProgress'
,
		 callSequence : 			''
,
		 extnType : 			'MODIFY'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

