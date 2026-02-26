package com.sports.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.management.dto.AttendanceDTO;
import com.sports.management.dto.GradeDTO;
import com.sports.management.entity.Student;
import com.sports.management.mapper.AttendanceMapper;
import com.sports.management.mapper.GradeMapper;
import com.sports.management.mapper.StudentMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @GetMapping("/students")
    public void exportStudents(HttpServletResponse response) throws IOException {
        List<Student> students = studentMapper.selectList(null);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学员信息");
        
        String[] headers = {"ID", "姓名", "性别", "年龄", "电话", "邮箱", "地址", "紧急联系人", "紧急电话"};
        createHeader(sheet, headers);
        
        int rowNum = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getGender());
            row.createCell(3).setCellValue(student.getAge() != null ? student.getAge() : 0);
            row.createCell(4).setCellValue(student.getPhone());
            row.createCell(5).setCellValue(student.getEmail());
            row.createCell(6).setCellValue(student.getAddress());
            row.createCell(7).setCellValue(student.getEmergencyContact());
            row.createCell(8).setCellValue(student.getEmergencyPhone());
        }
        
        autoSizeColumns(sheet, headers.length);
        
        setResponseHeader(response, "学员信息.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/attendance")
    public void exportAttendance(HttpServletResponse response) throws IOException {
        // Limit to reasonable number for demo
        Page<AttendanceDTO> page = new Page<>(1, 10000);
        List<AttendanceDTO> list = attendanceMapper.selectAttendancePage(page, null, null, null).getRecords();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("考勤记录");
        
        String[] headers = {"学员姓名", "课程名称", "日期", "状态", "备注"};
        createHeader(sheet, headers);
        
        int rowNum = 1;
        for (AttendanceDTO dto : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getStudentName());
            row.createCell(1).setCellValue(dto.getCourseName());
            row.createCell(2).setCellValue(dto.getAttendanceDate().toString());
            row.createCell(3).setCellValue(dto.getStatus());
            row.createCell(4).setCellValue(dto.getNote());
        }
        
        autoSizeColumns(sheet, headers.length);
        
        setResponseHeader(response, "考勤记录.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/grades")
    public void exportGrades(HttpServletResponse response) throws IOException {
        Page<GradeDTO> page = new Page<>(1, 10000);
        List<GradeDTO> list = gradeMapper.selectGradePage(page, null, null, null).getRecords();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("成绩记录");
        
        String[] headers = {"学员姓名", "课程名称", "成绩类型", "得分", "满分", "日期", "评语"};
        createHeader(sheet, headers);
        
        int rowNum = 1;
        for (GradeDTO dto : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getStudentName());
            row.createCell(1).setCellValue(dto.getCourseName());
            row.createCell(2).setCellValue(dto.getGradeType());
            row.createCell(3).setCellValue(dto.getScore().doubleValue());
            row.createCell(4).setCellValue(dto.getMaxScore().doubleValue());
            row.createCell(5).setCellValue(dto.getGradeDate().toString());
            row.createCell(6).setCellValue(dto.getComment());
        }
        
        autoSizeColumns(sheet, headers.length);
        
        setResponseHeader(response, "成绩记录.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void createHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.CellStyle style = sheet.getWorkbook().createCellStyle();
        org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }
    
    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void setResponseHeader(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileNameUrl = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileNameUrl);
    }
}
