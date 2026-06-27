package com.dom.project.service.impl;

import com.dom.project.entity.Affiliation;
import com.dom.project.entity.Employee;
import com.dom.project.entity.common.PageResult;
import com.dom.project.entity.dto.EmployeeSaveDTO;
import com.dom.project.entity.view.EmployeeListView;
import com.dom.project.entity.view.FirstUseDateView;
import com.dom.project.entity.vo.TotalUsageDaysVO;
import com.dom.project.enum_.EmployeeCategoryEnum;
import com.dom.project.enum_.OperationTypeEnum;
import com.dom.project.enum_.TargetTableEnum;
import com.dom.project.exception.BusinessException;
import com.dom.project.mapper.AffiliationMapper;
import com.dom.project.mapper.EmployeeMapper;
import com.dom.project.mapper.ResidenceHistoryMapper;
import com.dom.project.service.EmployeeService;
import com.dom.project.util.JsonUtils;
import com.dom.project.util.OperationLogWriter;
import com.dom.project.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 社員業務サービス実装。
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final AffiliationMapper affiliationMapper;
    private final ResidenceHistoryMapper residenceHistoryMapper;
    private final OperationLogWriter operationLogWriter;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper,
                               AffiliationMapper affiliationMapper,
                               ResidenceHistoryMapper residenceHistoryMapper,
                               OperationLogWriter operationLogWriter) {
        this.employeeMapper = employeeMapper;
        this.affiliationMapper = affiliationMapper;
        this.residenceHistoryMapper = residenceHistoryMapper;
        this.operationLogWriter = operationLogWriter;
    }

    @Override
    public PageResult<EmployeeListView> list(String keyword, String gender, String employeeCategory,
                                             String affiliationId, Boolean notResidingOnly,
                                             Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<EmployeeListView> list = employeeMapper.searchList(keyword, gender, employeeCategory,
                affiliationId, notResidingOnly, offset, limit);
        Long total = employeeMapper.countSearch(keyword, gender, employeeCategory, affiliationId, notResidingOnly);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public Employee getById(String employeeId) {
        Employee employee = employeeMapper.findById(employeeId);
        if (employee == null) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "社員が見つかりません");
        }
        return employee;
    }

    @Override
    @Transactional
    public Employee create(EmployeeSaveDTO dto) {
        if (!StringUtils.hasText(dto.getEmployeeId())) {
            throw new BusinessException("EMPLOYEE_ID_REQUIRED", "社員IDを入力してください");
        }
        String employeeId = dto.getEmployeeId().trim();
        if (employeeMapper.findById(employeeId) != null) {
            throw new BusinessException("EMPLOYEE_ID_DUPLICATE", "社員IDが重複しています");
        }
        validateSaveDto(dto);

        LocalDateTime now = LocalDateTime.now();
        Employee entity = toEntity(dto);
        entity.setEmployeeId(employeeId);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        employeeMapper.insert(entity);
        operationLogWriter.logCreate(
                OperationTypeEnum.EMPLOYEE_CREATE, TargetTableEnum.EMPLOYEE, employeeId, entity);
        return entity;
    }

    @Override
    @Transactional
    public Employee update(String employeeId, EmployeeSaveDTO dto) {
        Employee before = employeeMapper.findById(employeeId);
        if (before == null) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "社員が見つかりません");
        }
        validateSaveDto(dto);

        Employee entity = toEntity(dto);
        entity.setEmployeeId(employeeId);
        entity.setUpdatedAt(LocalDateTime.now());
        employeeMapper.update(entity);

        Employee after = employeeMapper.findById(employeeId);
        operationLogWriter.logUpdate(
                OperationTypeEnum.EMPLOYEE_UPDATE, TargetTableEnum.EMPLOYEE, employeeId, before, after);
        return after;
    }

    @Override
    @Transactional
    public void delete(String employeeId) {
        Employee before = employeeMapper.findById(employeeId);
        if (before == null) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "社員が見つかりません");
        }
        Long usedCount = residenceHistoryMapper.countByEmployeeId(employeeId);
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException("EMPLOYEE_IN_USE", "入居履歴が存在する社員は削除できません");
        }
        employeeMapper.logicalDelete(employeeId, LocalDateTime.now());
        operationLogWriter.logDelete(
                OperationTypeEnum.EMPLOYEE_DELETE, TargetTableEnum.EMPLOYEE, employeeId, before);
    }

    @Override
    public PageResult<Employee> search(String keyword, Integer page, Integer size) {
        int limit = PageUtils.limit(size);
        int offset = PageUtils.offset(page, limit);
        List<Employee> list = employeeMapper.searchByKeyword(keyword, offset, limit);
        Long total = employeeMapper.countByKeyword(keyword);
        return PageResult.of(list == null ? Collections.emptyList() : list, total, limit);
    }

    @Override
    public FirstUseDateView getFirstUseDate(String empId) {
        Employee employee = employeeMapper.findById(empId);
        if (employee == null) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "社員が見つかりません");
        }
        FirstUseDateView view = residenceHistoryMapper.findFirstUseDate(empId);
        if (view == null) {
            view = new FirstUseDateView();
        }
        view.setEmployeeName(employee.getName());
        view.setEmployeeCategory(employee.getEmployeeCategory());
        return view;
    }

    @Override
    public TotalUsageDaysVO getTotalUsageDays(String empId) {
        Employee employee = employeeMapper.findById(empId);
        if (employee == null) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "社員が見つかりません");
        }
        Integer days = residenceHistoryMapper.countTotalUsageDays(empId);
        return new TotalUsageDaysVO(days == null ? 0 : days);
    }

    private void validateSaveDto(EmployeeSaveDTO dto) {
        if (!"MALE".equals(dto.getGender()) && !"FEMALE".equals(dto.getGender())) {
            throw new BusinessException("INVALID_GENDER", "性別が不正です");
        }
        if (!EmployeeCategoryEnum.JAPAN.getCode().equals(dto.getEmployeeCategory())
                && !EmployeeCategoryEnum.CHINA_ASSIGN.getCode().equals(dto.getEmployeeCategory())) {
            throw new BusinessException("INVALID_EMPLOYEE_CATEGORY", "入居者区分が不正です");
        }
        if (StringUtils.hasText(dto.getAffiliationId())) {
            Affiliation affiliation = affiliationMapper.findById(dto.getAffiliationId());
            if (affiliation == null) {
                throw new BusinessException("AFFILIATION_NOT_FOUND", "所属が見つかりません");
            }
        }
    }

    private Employee toEntity(EmployeeSaveDTO dto) {
        Employee entity = new Employee();
        entity.setName(dto.getName().trim());
        entity.setGender(dto.getGender());
        entity.setEmployeeCategory(dto.getEmployeeCategory());
        entity.setAffiliationId(StringUtils.hasText(dto.getAffiliationId()) ? dto.getAffiliationId() : null);
        entity.setBusinessDivision(blankToNull(dto.getBusinessDivision()));
        entity.setNearestStation1(blankToNull(dto.getNearestStation1()));
        entity.setNearestStation2(blankToNull(dto.getNearestStation2()));
        entity.setNearestStation3(blankToNull(dto.getNearestStation3()));
        entity.setContactInfo(buildContactInfo(dto.getMobilePhone(), dto.getEmail()));
        return entity;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String buildContactInfo(String mobilePhone, String email) {
        if (!StringUtils.hasText(mobilePhone) && !StringUtils.hasText(email)) {
            return null;
        }
        Map<String, String> contact = new LinkedHashMap<>();
        if (StringUtils.hasText(mobilePhone)) {
            contact.put("mobilePhone", mobilePhone.trim());
        }
        if (StringUtils.hasText(email)) {
            contact.put("email", email.trim());
        }
        return JsonUtils.toJson(contact);
    }
}
