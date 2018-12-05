Feature: The validation system shall perform checks for possible clone cards - where no previous event was in system
  Scenario: Card used at location "CIT Library North Ground Exit, Cork, Ireland" where no events found at other locations
    Given card "7707775e-15ac-415f-a99c-e978856c8ec0" used at panel "7907775e-15ac-415f-a99c-e978856c8ec0"
    When check performed
    Then responds with validEvent "true"
    And no previous events found
    And responds within less than 1 seconds
