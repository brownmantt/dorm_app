import holiday_jp from '@holiday-jp/holiday_jp'
import { dateInMonth } from '@/utils/date'

/**
 * 寮割カレンダーで赤文字表示する対象日（土日・祝日）か判定する。
 * @param {string} yearMonth YYYY-MM
 * @param {number} day 1-based day of month
 * @param {string} [weekdayLabel] 曜日ラベル（日/月/…）
 * @returns {boolean}
 */
export function isCalendarOffDay(yearMonth, day, weekdayLabel) {
  if (weekdayLabel === '土' || weekdayLabel === '日') {
    return true
  }
  return holiday_jp.isHoliday(dateInMonth(yearMonth, day))
}
