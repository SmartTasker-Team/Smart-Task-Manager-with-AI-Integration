package com.smarttask.manager.application.dto;
/**
 * <h2>LoginRequestDTO</h2>
 *
 * <p>
 * DTO carrying user credentials required for authentication.
 * </p>
 *
 * <h3>Layer</h3>
 * <p>Application Layer (DTO)</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Transport login data from UI to use case</li>
 *   <li>Isolate UI input from business logic</li>
 * </ul>
 */


public record LoginRequestDTO(
        String email,
        String password
) {}
