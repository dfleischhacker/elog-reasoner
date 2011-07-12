Maximize
  0.5 sAxiomHidden|
Subject To
 R0: hAxiomHidden| >= 1
 R1: DisjointClasses(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>) >= 1
 R2: SubClassOf(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>) >= 1
 R3: SubClassOf(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Contributed_Talk>)
   >= 1
 R4: SubClassOf(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   >= 1
 R5: SubClassOf(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Conference_Session>)
   >= 1
 R6:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Regular_Session>)
   + SubClassOf(<http_//ekaw/Regular_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R7: DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Regular_Session>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Regular_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   >= 1
 R8: DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Regular_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Regular_Session>_<http_//ekaw/Conference_Session>)
   >= 1
 R9: DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Regular_Session>_<http_//ekaw/Conference_Session>)
   + SubClassOf(<http_//ekaw/Regular_Session>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   >= 1
 R10: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Regular_Session>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Regular_Session>)
   >= 1
 R11: DisjointClasses(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Document>) >= 1
 R12: DisjointClasses(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Paper>) >= 1
 R13: DisjointClasses(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Document>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasReviewer>_<http_//ekaw/Thing>)_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Assigned_Paper>_ObjectSomeValuesFrom(<http_//ekaw/hasReviewer>_<http_//ekaw/Thing>))
   >= 1
 R14: DisjointClasses(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Assigned_Paper>_ObjectSomeValuesFrom(<http_//ekaw/hasReviewer>_<http_//ekaw/Thing>))
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasReviewer>_<http_//ekaw/Thing>)_<http_//ekaw/Paper>)
   >= 1
 R15: SubClassOf(<http_//ekaw/Poster_Session>_<http_//ekaw/Session>)
   + DisjointClasses(<http_//ekaw/Poster_Session>_<http_//ekaw/Session>)
   >= 1
 R16: DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Poster_Session>)
   + SubClassOf(<http_//ekaw/Poster_Session>_<http_//ekaw/Event>) >= 1
 R17: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Poster_Session>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Poster_Session>)
   >= 1
 R18: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Poster_Session>)
   + SubClassOf(<http_//ekaw/Poster_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R19: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Poster_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R20: SubClassOf(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
   + DisjointClasses(<http_//ekaw/Abstract>_<http_//ekaw/Document>) >= 1
 R21: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/Paper_Author>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Paper_Author>_<http_//ekaw/Person>) >= 1
 R22:
   DisjointClasses(<http_//ekaw/Proceedings>_<http_//ekaw/Scientific_Event>)
   + SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Proceedings>)
   >= 1
 R23: SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Proceedings>)
   + DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Proceedings>)
   + SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Event>) >= 1
 R24: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Scientific_Event>)
   >= 1
 R25: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Event>) >= 1
 R26: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Scientific_Event>)
   >= 1
 R27: DisjointClasses(<http_//ekaw/Demo_Session>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Demo_Session>_<http_//ekaw/Event>) >= 1
 R28:
   DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Demo_Session>)
   + SubClassOf(<http_//ekaw/Demo_Session>_<http_//ekaw/Conference_Session>)
   >= 1
 R29: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Demo_Session>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Demo_Session>)
   >= 1
 R30: DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Demo_Session>_<http_//ekaw/Conference_Session>)
   + SubClassOf(<http_//ekaw/Demo_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R31: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Demo_Session>)
   + SubClassOf(<http_//ekaw/Demo_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R32:
   SubClassOf(<http_//ekaw/Research_Institute>_<http_//ekaw/Organisation>)
   + DisjointClasses(<http_//ekaw/Organisation>_<http_//ekaw/Research_Institute>)
   >= 1
 R33: SubClassOf(<http_//ekaw/Workshop_Session>_<http_//ekaw/Workshop>)
   + SubClassOf(<http_//ekaw/Workshop_Session>_<http_//ekaw/Session>)
   + DisjointClasses(<http_//ekaw/Session>_<http_//ekaw/Workshop>) >= 1
 R34:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Workshop_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Workshop_Session>)
   >= 1
 R35:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Workshop_Session>)
   + SubClassOf(<http_//ekaw/Workshop_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R36: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Workshop_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Workshop_Session>)
   >= 1
 R37: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Workshop_Session>_<http_//ekaw/Session>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Workshop_Session>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R38: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   >= 1
 R39: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   >= 1
 R40: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   >= 1
 R41: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   >= 1
 R42: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Contributed_Talk>)
   >= 1
 R43: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>) >= 1
 R44: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   >= 1
 R45: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference_Session>_<http_//ekaw/Session>)
   >= 1
 R46: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Individual_Presentation>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Individual_Presentation>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>))
   >= 1
 R47: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Individual_Presentation>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>))
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Scientific_Event>)
   + DisjointClasses(<http_//ekaw/Individual_Presentation>_<http_//ekaw/Scientific_Event>)
   >= 1
 R48: DisjointClasses(<http_//ekaw/Conference>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Session>) >= 1
 R49: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Conference>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Event>) >= 1
 R50: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Event>) >= 1
 R51: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Event>) >= 1
 R52: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Conference>_<http_//ekaw/Conference_Session>)
   >= 1
 R53: SubClassOf(<http_//ekaw/Tutorial>_<http_//ekaw/Session>)
   + DisjointClasses(<http_//ekaw/Session>_<http_//ekaw/Tutorial>) >= 1
 R54:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Tutorial>_<http_//ekaw/Session>)
   + DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Tutorial>) >= 1
 R55:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Tutorial>_<http_//ekaw/Session>)
   + DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Tutorial>) >= 1
 R56: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Tutorial>_<http_//ekaw/Session>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Tutorial>)
   >= 1
 R57: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Tutorial>_<http_//ekaw/Session>) >= 1
 R58: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Social_Event>_<http_//ekaw/Event>) >= 1
 R59: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Social_Event>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Social_Event>)
   >= 1
 R60: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + SubClassOf(<http_//ekaw/Social_Event>_<http_//ekaw/Event>) >= 1
 R61: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference_Session>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Social_Event>_<http_//ekaw/Event>) >= 1
 R62: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Individual_Presentation>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Social_Event>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Individual_Presentation>_<http_//ekaw/Social_Event>)
   >= 1
 R63:
   SubClassOf(<http_//ekaw/Invited_Speaker>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/Invited_Speaker>_<http_//ekaw/Possible_Reviewer>)
   >= 1
 R64: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/Invited_Speaker>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Invited_Speaker>_<http_//ekaw/Person>) >= 1
 R65: SubClassOf(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>)
   >= 1
 R66: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>) >= 1
 R67: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Conference_Trip>_<http_//ekaw/Contributed_Talk>)
   >= 1
 R68: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + SubClassOf(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>) >= 1
 R69: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + SubClassOf(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Conference_Trip>)
   >= 1
 R70: SubClassOf(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
   + DisjointClasses(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Invited_Talk_Abstract>_<http_//ekaw/Abstract>)
   >= 1
 R71: SubClassOf(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Invited_Talk_Abstract>_<http_//ekaw/Abstract>)
   + DisjointClasses(<http_//ekaw/Document>_<http_//ekaw/Invited_Talk_Abstract>)
   >= 1
 R72: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/PC_Member>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/PC_Member>_<http_//ekaw/Person>) >= 1
 R73: SubClassOf(<http_//ekaw/Proceedings>_<http_//ekaw/Document>)
   + DisjointClasses(<http_//ekaw/Document>_<http_//ekaw/Proceedings>)
   >= 1
 R74: SubClassOf(<http_//ekaw/OC_Chair>_<http_//ekaw/Person>)
   + DisjointClasses(<http_//ekaw/OC_Chair>_<http_//ekaw/Person>) >= 1
 R75: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/OC_Chair>_<http_//ekaw/Person>)
   + DisjointClasses(<http_//ekaw/OC_Chair>_<http_//ekaw/Possible_Reviewer>)
   >= 1
 R76: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/Possible_Reviewer>_<http_//ekaw/SC_Member>)
   + SubClassOf(<http_//ekaw/SC_Member>_<http_//ekaw/Person>) >= 1
 R77: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/Possible_Reviewer>_<http_//ekaw/SC_Member>)
   + SubClassOf(<http_//ekaw/SC_Member>_<http_//ekaw/Agency_Staff_Member>)
   + SubClassOf(<http_//ekaw/Agency_Staff_Member>_<http_//ekaw/Person>)
   >= 1
 R78:
   SubClassOf(<http_//ekaw/PC_Chair>_<http_//ekaw/Conference_Participant>)
   + DisjointClasses(<http_//ekaw/Conference_Participant>_<http_//ekaw/PC_Chair>)
   >= 1
 R79:
   SubClassOf(<http_//ekaw/PC_Chair>_<http_//ekaw/Conference_Participant>)
   + SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/PC_Chair>_<http_//ekaw/Possible_Reviewer>)
   >= 1
 R80: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/PC_Chair>_<http_//ekaw/Conference_Participant>)
   + DisjointClasses(<http_//ekaw/PC_Chair>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Person>)
   >= 1
 R81: SubClassOf(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
   + DisjointClasses(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Tutorial_Abstract>_<http_//ekaw/Abstract>)
   >= 1
 R82: DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Conference_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R83: DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Conference_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   >= 1
 R84: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Conference_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   >= 1
 R85: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(<http_//ekaw/Conference_Session>_<http_//ekaw/Session>)
   >= 1
 R86: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference_Session>_<http_//ekaw/Session>)
   >= 1
 R87:
   SubClassOf(<http_//ekaw/Session_Chair>_<http_//ekaw/Conference_Participant>)
   + DisjointClasses(<http_//ekaw/Conference_Participant>_<http_//ekaw/Session_Chair>)
   >= 1
 R88: SubClassOf(<http_//ekaw/Session_Chair>_<http_//ekaw/Person>)
   + DisjointClasses(<http_//ekaw/Person>_<http_//ekaw/Session_Chair>)
   >= 1
 R89: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Session_Chair>_<http_//ekaw/Person>)
   + DisjointClasses(<http_//ekaw/Possible_Reviewer>_<http_//ekaw/Session_Chair>)
   >= 1
 R90: SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Person>)
   + SubClassOf(<http_//ekaw/Session_Chair>_<http_//ekaw/Conference_Participant>)
   + DisjointClasses(<http_//ekaw/Person>_<http_//ekaw/Session_Chair>)
   >= 1
 R91:
   SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Session_Chair>_<http_//ekaw/Conference_Participant>)
   + DisjointClasses(<http_//ekaw/Possible_Reviewer>_<http_//ekaw/Session_Chair>)
   >= 1
 R92:
   SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Conference_Participant>)
   >= 1
 R93: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + DisjointClasses(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Person>)
   >= 1
 R94: SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Person>)
   + DisjointClasses(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Possible_Reviewer>)
   + SubClassOf(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Conference_Participant>)
   >= 1
 R95:
   DisjointClasses(<http_//ekaw/Demo_Chair>_<http_//ekaw/Tutorial_Chair>)
   + SubClassOf(<http_//ekaw/Demo_Chair>_<http_//ekaw/Tutorial_Chair>)
   >= 1
 R96:
   DisjointClasses(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Document>)
   >= 1
 R97: DisjointClasses(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Paper>)
   + SubClassOf(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Paper>) >= 1
 R98: SubClassOf(<http_//ekaw/Paper>_<http_//ekaw/Document>)
   + DisjointClasses(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Paper>) >= 1
 R99:
   DisjointClasses(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Camera_Ready_Paper>_ObjectSomeValuesFrom(<http_//ekaw/updatedVersionOf>_<http_//ekaw/Thing>))
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/updatedVersionOf>_<http_//ekaw/Thing>)_<http_//ekaw/Document>)
   >= 1
 R100:
   DisjointClasses(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Document>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/writtenBy>_<http_//ekaw/Thing>)_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Camera_Ready_Paper>_ObjectSomeValuesFrom(<http_//ekaw/writtenBy>_<http_//ekaw/Thing>))
   >= 1
 R101: SubClassOf(<http_//ekaw/Proceedings>_<http_//ekaw/Document>)
   + DisjointClasses(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Proceedings>)
   >= 1
 R102: SubClassOf(<http_//ekaw/Proceedings>_<http_//ekaw/Document>)
   + SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Proceedings>)
   + SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Document>)
   >= 1
 R103: SubClassOf(<http_//ekaw/Conference_Session>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Session>)
   >= 1
 R104:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Conference_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Event>)
   >= 1
 R105:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Conference_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Event>)
   >= 1
 R106:
   SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Conference_Session>_<http_//ekaw/Session>)
   >= 1
 R107:
   SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Contributed_Talk>)
   >= 1
 R108:
   DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Individual_Presentation>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Individual_Presentation>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>))
   >= 1
 R109: DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>))
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   >= 1
 R110:
   SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Individual_Presentation>)
   + SubClassOf(<http_//ekaw/Contributed_Talk>_ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>))
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Scientific_Event>)
   + DisjointClasses(<http_//ekaw/Individual_Presentation>_<http_//ekaw/Scientific_Event>)
   >= 1
 R111:
   SubClassOf(<http_//ekaw/Industrial_Session>_<http_//ekaw/Conference_Session>)
   + DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Industrial_Session>)
   >= 1
 R112: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Industrial_Session>)
   + SubClassOf(<http_//ekaw/Industrial_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
   >= 1
 R113: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Industrial_Session>)
   + SubClassOf(<http_//ekaw/Industrial_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   >= 1
 R114: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Industrial_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + SubClassOf(<http_//ekaw/Industrial_Session>_<http_//ekaw/Session>)
   >= 1
 R115: SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
   + SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
   + SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
   + DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
   + SubClassOf(<http_//ekaw/Industrial_Session>_<http_//ekaw/Session>)
   >= 1
Bounds
Binaries
 sAxiomHidden| hAxiomHidden|
 DisjointClasses(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>)
 SubClassOf(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Event>)
 SubClassOf(<http_//ekaw/Event>_<http_//ekaw/Contributed_Talk>)
 DisjointClasses(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Contributed_Talk>)
 DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Event>)
 SubClassOf(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Conference_Session>)
 DisjointClasses(<http_//ekaw/Conference_Banquet>_<http_//ekaw/Conference_Session>)
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Regular_Session>)
 SubClassOf(<http_//ekaw/Regular_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
 SubClassOf(<http_//ekaw/Regular_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
 SubClassOf(<http_//ekaw/Regular_Session>_<http_//ekaw/Conference_Session>)
 SubClassOf(<http_//ekaw/Regular_Session>_<http_//ekaw/Session>)
 SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Regular_Session>)
 DisjointClasses(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Paper>_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Assigned_Paper>_<http_//ekaw/Paper>)
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasReviewer>_<http_//ekaw/Thing>)_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Assigned_Paper>_ObjectSomeValuesFrom(<http_//ekaw/hasReviewer>_<http_//ekaw/Thing>))
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/hasReviewer>_<http_//ekaw/Thing>)_<http_//ekaw/Paper>)
 SubClassOf(<http_//ekaw/Poster_Session>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Poster_Session>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Poster_Session>)
 SubClassOf(<http_//ekaw/Poster_Session>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Poster_Session>)
 SubClassOf(<http_//ekaw/Poster_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
 SubClassOf(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
 DisjointClasses(<http_//ekaw/Abstract>_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Person>_<http_//ekaw/Possible_Reviewer>)
 DisjointClasses(<http_//ekaw/Paper_Author>_<http_//ekaw/Possible_Reviewer>)
 SubClassOf(<http_//ekaw/Paper_Author>_<http_//ekaw/Person>)
 DisjointClasses(<http_//ekaw/Proceedings>_<http_//ekaw/Scientific_Event>)
 SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Proceedings>)
 DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Proceedings>)
 SubClassOf(<http_//ekaw/Scientific_Event>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Scientific_Event>)
 DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Scientific_Event>)
 DisjointClasses(<http_//ekaw/Demo_Session>_<http_//ekaw/Event>)
 SubClassOf(<http_//ekaw/Demo_Session>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Demo_Session>)
 SubClassOf(<http_//ekaw/Demo_Session>_<http_//ekaw/Conference_Session>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Demo_Session>)
 SubClassOf(<http_//ekaw/Demo_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
 SubClassOf(<http_//ekaw/Research_Institute>_<http_//ekaw/Organisation>)
 DisjointClasses(<http_//ekaw/Organisation>_<http_//ekaw/Research_Institute>)
 SubClassOf(<http_//ekaw/Workshop_Session>_<http_//ekaw/Workshop>)
 SubClassOf(<http_//ekaw/Workshop_Session>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Session>_<http_//ekaw/Workshop>)
 SubClassOf(<http_//ekaw/Workshop_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
 DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Workshop_Session>)
 SubClassOf(<http_//ekaw/Workshop_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Workshop_Session>)
 SubClassOf(<http_//ekaw/Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Contributed_Talk>)
 SubClassOf(<http_//ekaw/Conference_Session>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Individual_Presentation>)
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Individual_Presentation>)
 SubClassOf(<http_//ekaw/Contributed_Talk>_ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>))
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Scientific_Event>)
 DisjointClasses(<http_//ekaw/Individual_Presentation>_<http_//ekaw/Scientific_Event>)
 DisjointClasses(<http_//ekaw/Conference>_<http_//ekaw/Session>)
 SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Conference>_<http_//ekaw/Contributed_Talk>)
 SubClassOf(<http_//ekaw/Conference>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Conference>_<http_//ekaw/Conference_Session>)
 SubClassOf(<http_//ekaw/Tutorial>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Session>_<http_//ekaw/Tutorial>)
 DisjointClasses(<http_//ekaw/Event>_<http_//ekaw/Tutorial>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Tutorial>)
 SubClassOf(<http_//ekaw/Social_Event>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Social_Event>)
 DisjointClasses(<http_//ekaw/Individual_Presentation>_<http_//ekaw/Social_Event>)
 SubClassOf(<http_//ekaw/Invited_Speaker>_<http_//ekaw/Possible_Reviewer>)
 DisjointClasses(<http_//ekaw/Invited_Speaker>_<http_//ekaw/Possible_Reviewer>)
 SubClassOf(<http_//ekaw/Invited_Speaker>_<http_//ekaw/Person>)
 SubClassOf(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Conference_Trip>_<http_//ekaw/Event>)
 DisjointClasses(<http_//ekaw/Conference_Trip>_<http_//ekaw/Contributed_Talk>)
 DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Conference_Trip>)
 SubClassOf(<http_//ekaw/Invited_Talk_Abstract>_<http_//ekaw/Abstract>)
 DisjointClasses(<http_//ekaw/Document>_<http_//ekaw/Invited_Talk_Abstract>)
 DisjointClasses(<http_//ekaw/PC_Member>_<http_//ekaw/Possible_Reviewer>)
 SubClassOf(<http_//ekaw/PC_Member>_<http_//ekaw/Person>)
 SubClassOf(<http_//ekaw/Proceedings>_<http_//ekaw/Document>)
 DisjointClasses(<http_//ekaw/Document>_<http_//ekaw/Proceedings>)
 SubClassOf(<http_//ekaw/OC_Chair>_<http_//ekaw/Person>)
 DisjointClasses(<http_//ekaw/OC_Chair>_<http_//ekaw/Person>)
 DisjointClasses(<http_//ekaw/OC_Chair>_<http_//ekaw/Possible_Reviewer>)
 DisjointClasses(<http_//ekaw/Possible_Reviewer>_<http_//ekaw/SC_Member>)
 SubClassOf(<http_//ekaw/SC_Member>_<http_//ekaw/Person>)
 SubClassOf(<http_//ekaw/SC_Member>_<http_//ekaw/Agency_Staff_Member>)
 SubClassOf(<http_//ekaw/Agency_Staff_Member>_<http_//ekaw/Person>)
 SubClassOf(<http_//ekaw/PC_Chair>_<http_//ekaw/Conference_Participant>)
 DisjointClasses(<http_//ekaw/Conference_Participant>_<http_//ekaw/PC_Chair>)
 SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Possible_Reviewer>)
 DisjointClasses(<http_//ekaw/PC_Chair>_<http_//ekaw/Possible_Reviewer>)
 SubClassOf(<http_//ekaw/Conference_Participant>_<http_//ekaw/Person>)
 SubClassOf(<http_//ekaw/Tutorial_Abstract>_<http_//ekaw/Abstract>)
 SubClassOf(<http_//ekaw/Conference_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
 SubClassOf(<http_//ekaw/Conference_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
 SubClassOf(<http_//ekaw/Session_Chair>_<http_//ekaw/Conference_Participant>)
 DisjointClasses(<http_//ekaw/Conference_Participant>_<http_//ekaw/Session_Chair>)
 SubClassOf(<http_//ekaw/Session_Chair>_<http_//ekaw/Person>)
 DisjointClasses(<http_//ekaw/Person>_<http_//ekaw/Session_Chair>)
 DisjointClasses(<http_//ekaw/Possible_Reviewer>_<http_//ekaw/Session_Chair>)
 DisjointClasses(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Possible_Reviewer>)
 SubClassOf(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Conference_Participant>)
 SubClassOf(<http_//ekaw/Early-Registered_Participant>_<http_//ekaw/Person>)
 DisjointClasses(<http_//ekaw/Demo_Chair>_<http_//ekaw/Tutorial_Chair>)
 SubClassOf(<http_//ekaw/Demo_Chair>_<http_//ekaw/Tutorial_Chair>)
 DisjointClasses(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Document>)
 DisjointClasses(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Paper>)
 SubClassOf(<http_//ekaw/Camera_Ready_Paper>_<http_//ekaw/Paper>)
 SubClassOf(<http_//ekaw/Camera_Ready_Paper>_ObjectSomeValuesFrom(<http_//ekaw/updatedVersionOf>_<http_//ekaw/Thing>))
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/updatedVersionOf>_<http_//ekaw/Thing>)_<http_//ekaw/Document>)
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/writtenBy>_<http_//ekaw/Thing>)_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Camera_Ready_Paper>_ObjectSomeValuesFrom(<http_//ekaw/writtenBy>_<http_//ekaw/Thing>))
 DisjointClasses(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Document>)
 SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Proceedings>)
 SubClassOf(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Conference_Session>)
 DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Document>)
 DisjointClasses(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Session>)
 DisjointClasses(<http_//ekaw/Conference_Proceedings>_<http_//ekaw/Event>)
 SubClassOf(ObjectSomeValuesFrom(<http_//ekaw/presentationOfPaper>_<http_//ekaw/Thing>)_<http_//ekaw/Event>)
 SubClassOf(<http_//ekaw/Industrial_Session>_<http_//ekaw/Conference_Session>)
 DisjointClasses(<http_//ekaw/Conference_Session>_<http_//ekaw/Industrial_Session>)
 DisjointClasses(<http_//ekaw/Contributed_Talk>_<http_//ekaw/Industrial_Session>)
 SubClassOf(<http_//ekaw/Industrial_Session>_ObjectSomeValuesFrom(<http_//ekaw/partOfEvent>_<http_//ekaw/Thing>))
 SubClassOf(<http_//ekaw/Industrial_Session>_ObjectSomeValuesFrom(<http_//ekaw/hasEvent>_<http_//ekaw/Thing>))
 SubClassOf(<http_//ekaw/Industrial_Session>_<http_//ekaw/Session>)
End
