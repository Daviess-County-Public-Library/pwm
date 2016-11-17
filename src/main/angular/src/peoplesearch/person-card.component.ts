/*
 * Password Management Servlets (PWM)
 * http://www.pwm-project.org
 *
 * Copyright (c) 2006-2009 Novell, Inc.
 * Copyright (c) 2009-2016 The PWM Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


import { Component } from '../component';
import Person from '../models/person.model';
import { IPeopleService } from '../services/people.service';

@Component({
    bindings: {
        directReports: '<',
        person: '<',
        size: '@',
        showDirectReportCount: '='
    },
    stylesheetUrl: require('peoplesearch/person-card.component.scss'),
    templateUrl: require('peoplesearch/person-card.component.html')
})
export default class PersonCardComponent {
    private details: any[]; // For large style cards
    private person: Person;
    private directReports: Person[];
    private size: string;
    private showDirectReportCount: boolean;

    static $inject = ['PeopleService'];
    constructor(private peopleService: IPeopleService) {
        this.details = [];
        this.size = 'medium';
    }

    $onChanges(): void {
        if (this.person) {
            this.setDisplayData();

            if (this.showDirectReportCount) {
                this.peopleService.getNumberOfDirectReports(this.person.userKey)
                    .then((numDirectReports) => {
                        this.person.numDirectReports = numDirectReports;
                    }).catch((result) => {
                    console.log(result);
                });
            }
        }
    }

    getAvatarStyle(): any {
        if (this.person && this.person.photoURL) {
            return { 'background-image': 'url(' + this.person.photoURL + ')' };
        }

        return {};
    }

    private setDisplayData(): void {
        if (this.person.detail) {
            this.details = Object
                .keys(this.person.detail)
                .map((key: string) => {
                    return this.person.detail[key];
                });
        }

        if (this.directReports) {
            this.person.numDirectReports = this.directReports.length;
        }
    }
}
